package com.bx.implatform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bx.imclient.IMClient;
import com.bx.imcommon.model.IMPrivateMessage;
import com.bx.imcommon.model.IMUserInfo;
import com.bx.implatform.dto.TransferDTO;
import com.bx.implatform.entity.PrivateMessage;
import com.bx.implatform.entity.UserWallet;
import com.bx.implatform.entity.WalletTransaction;
import com.bx.implatform.enums.MessageStatus;
import com.bx.implatform.enums.MessageType;
import com.bx.implatform.enums.TransactionStatusEnum;
import com.bx.implatform.enums.TransactionTypeEnum;
import com.bx.implatform.exception.GlobalException;
import com.bx.implatform.mapper.PrivateMessageMapper;
import com.bx.implatform.mapper.UserWalletMapper;
import com.bx.implatform.service.TransferService;
import com.bx.implatform.service.UserWalletService;
import com.bx.implatform.service.WalletTransactionService;
import com.bx.implatform.session.SessionContext;
import com.bx.implatform.session.UserSession;
import com.bx.implatform.vo.PrivateMessageVO;
import com.bx.implatform.vo.TransferVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 转账服务实现
 *
 * @author blue
 * @since 2025-10-06
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TransferServiceImpl implements TransferService {

    private final UserWalletService userWalletService;
    private final UserWalletMapper userWalletMapper;
    private final WalletTransactionService walletTransactionService;
    private final PrivateMessageMapper privateMessageMapper;
    private final IMClient imClient;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TransferVO transfer(TransferDTO dto) {
        Long fromUserId = SessionContext.getSession().getUserId();
        Long toUserId = dto.getToUserId();

        // 验证不能给自己转账
        if (fromUserId.equals(toUserId)) {
            throw new GlobalException("不能给自己转账");
        }

        // 验证转账金额
        if (dto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new GlobalException("转账金额必须大于0");
        }

        // 验证支付密码
        if (!userWalletService.verifyPayPassword(fromUserId, dto.getPayPassword())) {
            throw new GlobalException("支付密码错误");
        }

        // 查询转账方钱包
        UserWallet fromWallet = userWalletService.getByUserId(fromUserId);
        
        // 检查余额是否充足
        if (fromWallet.getBalance().compareTo(dto.getAmount()) < 0) {
            throw new GlobalException("账户余额不足");
        }

        // 获取或创建收款方钱包
        UserWallet toWallet = userWalletService.getOrCreateWallet(toUserId);

        // 记录交易前余额
        BigDecimal beforeBalance = fromWallet.getBalance();

        // 创建交易流水（处理中状态）
        String transactionNo = walletTransactionService.createTransaction(
                fromUserId, toUserId, dto.getAmount(),
                TransactionTypeEnum.TRANSFER.getCode(),
                null, dto.getRemark()
        );

        // 执行转账：扣减转账方余额（使用乐观锁）
        int deductResult = userWalletMapper.deductBalance(
                fromUserId, dto.getAmount(), fromWallet.getVersion()
        );

        if (deductResult == 0) {
            log.error("扣减余额失败，可能是并发冲突, userId:{}, amount:{}", fromUserId, dto.getAmount());
            throw new GlobalException("转账失败，请重试");
        }

        // 增加收款方余额（使用乐观锁）
        int maxRetry = 3;
        boolean addSuccess = false;
        for (int i = 0; i < maxRetry; i++) {
            toWallet = userWalletService.getByUserId(toUserId);
            int addResult = userWalletMapper.addBalance(
                    toUserId, dto.getAmount(), toWallet.getVersion()
            );
            if (addResult > 0) {
                addSuccess = true;
                break;
            }
            log.warn("增加余额失败，重试中, userId:{}, retry:{}", toUserId, i + 1);
        }

        if (!addSuccess) {
            log.error("增加收款方余额失败, userId:{}, amount:{}", toUserId, dto.getAmount());
            throw new GlobalException("转账失败，请联系客服");
        }

        // 计算交易后余额
        BigDecimal afterBalance = beforeBalance.subtract(dto.getAmount());

        // 更新交易状态为成功
        walletTransactionService.updateTransactionStatus(
                transactionNo,
                TransactionStatusEnum.SUCCESS.getCode(),
                beforeBalance,
                afterBalance
        );

        log.info("转账成功, transactionNo:{}, from:{}, to:{}, amount:{}",
                transactionNo, fromUserId, toUserId, dto.getAmount());

        // 构造返回结果
        TransferVO vo = new TransferVO();
        vo.setTransactionNo(transactionNo);
        vo.setFromUserId(fromUserId);
        vo.setToUserId(toUserId);
        vo.setAmount(dto.getAmount());
        vo.setBeforeBalance(beforeBalance);
        vo.setAfterBalance(afterBalance);
        vo.setRemark(dto.getRemark());
        vo.setTransferTime(new Date());

        // 创建转账消息记录并推送
        UserSession session = SessionContext.getSession();
        createAndSendTransferMessage(transactionNo, session, toUserId);

        return vo;
    }

    @Override
    public TransferVO getTransferDetail(String transactionNo) {
        // 查询交易流水
        LambdaQueryWrapper<WalletTransaction> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WalletTransaction::getTransactionNo, transactionNo);
        WalletTransaction transaction = walletTransactionService.getOne(wrapper);
        
        if (transaction == null) {
            throw new GlobalException("转账记录不存在");
        }
        
        // 验证交易类型
        if (!TransactionTypeEnum.TRANSFER.getCode().equals(transaction.getTransactionType())) {
            throw new GlobalException("该流水号不是转账记录");
        }
        
        // 转换为VO
        TransferVO vo = new TransferVO();
        vo.setTransactionNo(transaction.getTransactionNo());
        vo.setFromUserId(transaction.getFromUserId());
        vo.setToUserId(transaction.getToUserId());
        vo.setAmount(transaction.getAmount());
        vo.setBeforeBalance(transaction.getBeforeBalance());
        vo.setAfterBalance(transaction.getAfterBalance());
        vo.setRemark(transaction.getRemark());
        vo.setTransferTime(transaction.getCreatedAt());
        
        return vo;
    }

    /**
     * 创建转账消息记录并推送
     */
    private void createAndSendTransferMessage(String transactionNo, UserSession session, Long toUserId) {
        // 创建私聊消息
        PrivateMessage msg = new PrivateMessage();
        msg.setSendId(session.getUserId());
        msg.setRecvId(toUserId);
        msg.setContent(transactionNo); // 存储交易流水号
        msg.setType(MessageType.TRANSFER.code());
        msg.setStatus(MessageStatus.PENDING.code());
        msg.setSendTime(new Date());
        privateMessageMapper.insert(msg);
        
        // 推送消息
        PrivateMessageVO msgVO = new PrivateMessageVO();
        msgVO.setId(msg.getId());
        msgVO.setSendId(msg.getSendId());
        msgVO.setRecvId(msg.getRecvId());
        msgVO.setContent(msg.getContent());
        msgVO.setType(msg.getType());
        msgVO.setStatus(msg.getStatus());
        msgVO.setSendTime(msg.getSendTime());
        
        IMPrivateMessage<PrivateMessageVO> sendMessage = new IMPrivateMessage<>();
        sendMessage.setSender(new IMUserInfo(session.getUserId(), session.getTerminal()));
        sendMessage.setRecvId(toUserId);
        sendMessage.setSendToSelf(true);
        sendMessage.setData(msgVO);
        sendMessage.setSendResult(true);
        imClient.sendPrivateMessage(sendMessage);
        
        log.info("推送转账消息，发送id:{}, 接收id:{}, 交易号:{}", session.getUserId(), toUserId, transactionNo);
    }
}

