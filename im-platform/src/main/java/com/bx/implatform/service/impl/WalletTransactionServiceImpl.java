package com.bx.implatform.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bx.implatform.entity.User;
import com.bx.implatform.entity.WalletTransaction;
import com.bx.implatform.enums.TransactionStatusEnum;
import com.bx.implatform.enums.TransactionTypeEnum;
import com.bx.implatform.exception.GlobalException;
import com.bx.implatform.mapper.WalletTransactionMapper;
import com.bx.implatform.service.UserService;
import com.bx.implatform.service.WalletTransactionService;
import com.bx.implatform.session.SessionContext;
import com.bx.implatform.util.BeanUtils;
import com.bx.implatform.vo.WalletTransactionVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 交易流水服务实现
 *
 * @author blue
 * @since 2025-10-06
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WalletTransactionServiceImpl extends ServiceImpl<WalletTransactionMapper, WalletTransaction> 
        implements WalletTransactionService {

    private final UserService userService;

    @Override
    public String createTransaction(Long fromUserId, Long toUserId, BigDecimal amount,
                                    Integer transactionType, String relationId, String remark) {
        // 生成交易流水号
        String transactionNo = "TXN" + IdUtil.getSnowflakeNextIdStr();

        WalletTransaction transaction = new WalletTransaction();
        transaction.setTransactionNo(transactionNo);
        transaction.setFromUserId(fromUserId);
        transaction.setToUserId(toUserId);
        transaction.setAmount(amount);
        transaction.setTransactionType(transactionType);
        transaction.setStatus(TransactionStatusEnum.PROCESSING.getCode());
        transaction.setRelationId(relationId);
        transaction.setRemark(remark);
        transaction.setFee(BigDecimal.ZERO);

        this.save(transaction);
        log.info("创建交易流水成功, transactionNo:{}, fromUserId:{}, toUserId:{}, amount:{}", 
                transactionNo, fromUserId, toUserId, amount);

        return transactionNo;
    }

    @Override
    public void updateTransactionStatus(String transactionNo, Integer status,
                                        BigDecimal beforeBalance, BigDecimal afterBalance) {
        LambdaQueryWrapper<WalletTransaction> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WalletTransaction::getTransactionNo, transactionNo);
        
        WalletTransaction transaction = this.getOne(wrapper);
        if (transaction == null) {
            throw new GlobalException("交易记录不存在");
        }

        transaction.setStatus(status);
        transaction.setBeforeBalance(beforeBalance);
        transaction.setAfterBalance(afterBalance);

        this.updateById(transaction);
        log.info("更新交易状态成功, transactionNo:{}, status:{}", transactionNo, status);
    }

    @Override
    public List<WalletTransactionVO> getTransactionList(Integer page, Integer size) {
        Long userId = SessionContext.getSession().getUserId();

        // 查询当前用户相关的交易记录
        LambdaQueryWrapper<WalletTransaction> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w -> w.eq(WalletTransaction::getFromUserId, userId)
                         .or()
                         .eq(WalletTransaction::getToUserId, userId));
        wrapper.orderByDesc(WalletTransaction::getCreatedAt);

        Page<WalletTransaction> pageResult = this.page(new Page<>(page, size), wrapper);
        List<WalletTransaction> transactions = pageResult.getRecords();

        if (transactions.isEmpty()) {
            return List.of();
        }

        // 获取所有相关用户ID
        Set<Long> userIds = transactions.stream()
                .flatMap(t -> List.of(t.getFromUserId(), t.getToUserId()).stream())
                .collect(Collectors.toSet());

        // 批量查询用户信息
        Map<Long, User> userMap = userService.listByIds(userIds).stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));

        // 转换为VO
        return transactions.stream().map(transaction -> {
            WalletTransactionVO vo = BeanUtils.copyProperties(transaction, WalletTransactionVO.class);
            
            // 设置用户昵称
            User fromUser = userMap.get(transaction.getFromUserId());
            User toUser = userMap.get(transaction.getToUserId());
            if (fromUser != null) {
                vo.setFromUserName(fromUser.getNickName());
            }
            if (toUser != null) {
                vo.setToUserName(toUser.getNickName());
            }

            // 设置类型描述
            TransactionTypeEnum typeEnum = TransactionTypeEnum.fromCode(transaction.getTransactionType());
            if (typeEnum != null) {
                vo.setTransactionTypeDesc(typeEnum.getDesc());
            }

            // 设置状态描述
            TransactionStatusEnum statusEnum = TransactionStatusEnum.fromCode(transaction.getStatus());
            if (statusEnum != null) {
                vo.setStatusDesc(statusEnum.getDesc());
            }

            return vo;
        }).collect(Collectors.toList());
    }
}

