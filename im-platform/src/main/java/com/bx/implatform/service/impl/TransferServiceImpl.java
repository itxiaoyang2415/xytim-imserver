package com.bx.implatform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bx.imclient.IMClient;
import com.bx.imcommon.model.IMPrivateMessage;
import com.bx.imcommon.model.IMUserInfo;
import com.bx.implatform.annotation.RedisLock;
import com.bx.implatform.dto.ReceiveTransferDTO;
import com.bx.implatform.dto.TransferDTO;
import com.bx.implatform.entity.PrivateMessage;
import com.bx.implatform.entity.Transfer;
import com.bx.implatform.entity.UserWallet;
import com.bx.implatform.enums.MessageStatus;
import com.bx.implatform.enums.MessageType;
import com.bx.implatform.enums.TransactionStatusEnum;
import com.bx.implatform.enums.TransactionTypeEnum;
import com.bx.implatform.enums.TransferStatusEnum;
import com.bx.implatform.exception.GlobalException;
import com.bx.implatform.mapper.PrivateMessageMapper;
import com.bx.implatform.mapper.TransferMapper;
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
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

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
    private final TransferMapper transferMapper;
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
        userWalletService.getOrCreateWallet(toUserId);

        // 生成转账编号
        String transferNo = "TXF" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        // 计算过期时间（24小时后）
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, 24);
        Date expireTime = calendar.getTime();

        // 冻结转账方余额（使用乐观锁）
        int freezeResult = userWalletMapper.freezeBalance(
                fromUserId, dto.getAmount(), fromWallet.getVersion()
        );

        if (freezeResult == 0) {
            log.error("冻结余额失败，可能是并发冲突, userId:{}, amount:{}", fromUserId, dto.getAmount());
            throw new GlobalException("转账失败，请重试");
        }

        // 创建转账记录
        Transfer transfer = new Transfer();
        transfer.setTransferNo(transferNo);
        transfer.setFromUserId(fromUserId);
        transfer.setToUserId(toUserId);
        transfer.setAmount(dto.getAmount());
        transfer.setStatus(TransferStatusEnum.PENDING.getCode());
        transfer.setRemark(dto.getRemark());
        transfer.setExpireTime(expireTime);
        transfer.setCreatedAt(new Date());
        transfer.setUpdatedAt(new Date());
        transferMapper.insert(transfer);

        log.info("创建转账成功, transferNo:{}, from:{}, to:{}, amount:{}",
                transferNo, fromUserId, toUserId, dto.getAmount());

        // 构造返回结果
        TransferVO vo = new TransferVO();
        vo.setTransferNo(transferNo);
        vo.setFromUserId(fromUserId);
        vo.setToUserId(toUserId);
        vo.setAmount(dto.getAmount());
        vo.setStatus(TransferStatusEnum.PENDING.getCode());
        vo.setStatusDesc(TransferStatusEnum.PENDING.getDesc());
        vo.setRemark(dto.getRemark());
        vo.setTransferTime(new Date());
        vo.setExpireTime(expireTime);

        // 创建转账消息记录并推送
        UserSession session = SessionContext.getSession();
        createAndSendTransferMessage(transferNo, session, toUserId);

        return vo;
    }

    @Override
    @RedisLock(prefixKey = "transfer:receive:", key = "#dto.transferNo", waitTime = 0)
    @Transactional(rollbackFor = Exception.class)
    public TransferVO receiveTransfer(ReceiveTransferDTO dto) {
        Long receiverId = SessionContext.getSession().getUserId();

        // 查询转账记录
        LambdaQueryWrapper<Transfer> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Transfer::getTransferNo, dto.getTransferNo());
        Transfer transfer = transferMapper.selectOne(wrapper);

        if (transfer == null) {
            throw new GlobalException("转账不存在");
        }

        // 验证接收者
        if (!receiverId.equals(transfer.getToUserId())) {
            throw new GlobalException("无权领取该转账");
        }

        // 验证转账状态
        if (!TransferStatusEnum.PENDING.getCode().equals(transfer.getStatus())) {
            if (TransferStatusEnum.RECEIVED.getCode().equals(transfer.getStatus())) {
                throw new GlobalException("转账已被领取");
            } else if (TransferStatusEnum.EXPIRED.getCode().equals(transfer.getStatus())) {
                throw new GlobalException("转账已过期");
            } else if (TransferStatusEnum.REFUNDED.getCode().equals(transfer.getStatus())) {
                throw new GlobalException("转账已退款");
            }
            throw new GlobalException("转账状态异常");
        }

        // 验证是否过期
        if (new Date().after(transfer.getExpireTime())) {
            throw new GlobalException("转账已过期");
        }

        // 获取发送方和接收方钱包
        UserWallet fromWallet = userWalletService.getByUserId(transfer.getFromUserId());
        UserWallet toWallet = userWalletService.getByUserId(receiverId);

        // 解冻发送方余额并扣款
        int unfreezeResult = userWalletMapper.unfreezeAndDeductBalance(
                transfer.getFromUserId(),
                transfer.getAmount(),
                fromWallet.getVersion()
        );

        if (unfreezeResult == 0) {
            log.error("解冻并扣款失败, userId:{}, amount:{}", transfer.getFromUserId(), transfer.getAmount());
            throw new GlobalException("转账领取失败，请重试");
        }

        // 增加接收方余额（使用乐观锁，重试3次）
        int maxRetry = 3;
        boolean addSuccess = false;
        for (int i = 0; i < maxRetry; i++) {
            toWallet = userWalletService.getByUserId(receiverId);
            int addResult = userWalletMapper.addBalance(
                    receiverId, transfer.getAmount(), toWallet.getVersion()
            );
            if (addResult > 0) {
                addSuccess = true;
                break;
            }
            log.warn("增加余额失败，重试中, userId:{}, retry:{}", receiverId, i + 1);
        }

        if (!addSuccess) {
            log.error("增加收款方余额失败, userId:{}, amount:{}", receiverId, transfer.getAmount());
            throw new GlobalException("转账领取失败，请联系客服");
        }

        // 记录交易流水（转账方）
        BigDecimal fromBeforeBalance = fromWallet.getBalance().add(fromWallet.getFrozenBalance());
        BigDecimal fromAfterBalance = fromBeforeBalance.subtract(transfer.getAmount());
        String transactionNo = walletTransactionService.createTransaction(
                transfer.getFromUserId(), receiverId, transfer.getAmount(),
                TransactionTypeEnum.TRANSFER.getCode(),
                transfer.getTransferNo(), transfer.getRemark()
        );
        walletTransactionService.updateTransactionStatus(
                transactionNo,
                TransactionStatusEnum.SUCCESS.getCode(),
                fromBeforeBalance,
                fromAfterBalance
        );

        // 更新转账状态
        Date now = new Date();
        transfer.setStatus(TransferStatusEnum.RECEIVED.getCode());
        transfer.setReceiveTime(now);
        transfer.setTransactionNo(transactionNo);
        transfer.setUpdatedAt(now);
        transferMapper.updateById(transfer);

        log.info("转账领取成功, transferNo:{}, from:{}, to:{}, amount:{}",
                dto.getTransferNo(), transfer.getFromUserId(), receiverId, transfer.getAmount());

        // 构造返回结果
        return buildTransferVO(transfer, fromBeforeBalance, fromAfterBalance);
    }

    @Override
    public TransferVO getTransferDetail(String transferNo) {
        // 查询转账记录
        LambdaQueryWrapper<Transfer> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Transfer::getTransferNo, transferNo);
        Transfer transfer = transferMapper.selectOne(wrapper);
        
        if (transfer == null) {
            throw new GlobalException("转账记录不存在");
        }
        
        return buildTransferVO(transfer, null, null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void refundExpiredTransfers() {
        // 查询过期的待领取转账
        LambdaQueryWrapper<Transfer> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Transfer::getStatus, TransferStatusEnum.PENDING.getCode())
               .lt(Transfer::getExpireTime, new Date());
        
        List<Transfer> expiredTransfers = transferMapper.selectList(wrapper);

        for (Transfer transfer : expiredTransfers) {
            try {
                // 查询发送方钱包
                UserWallet fromWallet = userWalletService.getByUserId(transfer.getFromUserId());
                
                // 解冻并退还金额
                int result = userWalletMapper.unfreezeBalance(
                        transfer.getFromUserId(),
                        transfer.getAmount(),
                        fromWallet.getVersion()
                );

                if (result > 0) {
                    // 更新转账状态为已退款
                    Date now = new Date();
                    transfer.setStatus(TransferStatusEnum.REFUNDED.getCode());
                    transfer.setRefundTime(now);
                    transfer.setUpdatedAt(now);
                    transferMapper.updateById(transfer);

                    log.info("转账过期退款成功, transferNo:{}, amount:{}",
                            transfer.getTransferNo(), transfer.getAmount());
                } else {
                    log.error("转账过期退款失败（乐观锁冲突）, transferNo:{}", transfer.getTransferNo());
                }
            } catch (Exception e) {
                log.error("转账过期退款失败, transferNo:{}", transfer.getTransferNo(), e);
            }
        }
    }

    /**
     * 创建转账消息记录并推送
     */
    private void createAndSendTransferMessage(String transferNo, UserSession session, Long toUserId) {
        // 创建私聊消息
        PrivateMessage msg = new PrivateMessage();
        msg.setSendId(session.getUserId());
        msg.setRecvId(toUserId);
        msg.setContent(transferNo); // 存储转账编号
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
        
        log.info("推送转账消息，发送id:{}, 接收id:{}, 转账号:{}", session.getUserId(), toUserId, transferNo);
    }

    /**
     * 构建TransferVO
     */
    private TransferVO buildTransferVO(Transfer transfer, BigDecimal beforeBalance, BigDecimal afterBalance) {
        TransferVO vo = new TransferVO();
        vo.setTransferNo(transfer.getTransferNo());
        vo.setTransactionNo(transfer.getTransactionNo());
        vo.setFromUserId(transfer.getFromUserId());
        vo.setToUserId(transfer.getToUserId());
        vo.setAmount(transfer.getAmount());
        vo.setStatus(transfer.getStatus());
        vo.setStatusDesc(TransferStatusEnum.fromCode(transfer.getStatus()).getDesc());
        vo.setBeforeBalance(beforeBalance);
        vo.setAfterBalance(afterBalance);
        vo.setRemark(transfer.getRemark());
        vo.setTransferTime(transfer.getCreatedAt());
        vo.setExpireTime(transfer.getExpireTime());
        vo.setReceiveTime(transfer.getReceiveTime());
        vo.setRefundTime(transfer.getRefundTime());
        return vo;
    }
}
