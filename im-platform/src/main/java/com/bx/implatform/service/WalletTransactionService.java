package com.bx.implatform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bx.implatform.entity.WalletTransaction;
import com.bx.implatform.vo.WalletTransactionVO;

import java.math.BigDecimal;
import java.util.List;

/**
 * 交易流水服务接口
 *
 * @author blue
 * @since 2025-10-06
 */
public interface WalletTransactionService extends IService<WalletTransaction> {

    /**
     * 创建交易流水记录
     *
     * @param fromUserId 付款用户ID
     * @param toUserId 收款用户ID
     * @param amount 金额
     * @param transactionType 交易类型
     * @param relationId 关联业务ID
     * @param remark 备注
     * @return 交易流水号
     */
    String createTransaction(Long fromUserId, Long toUserId, BigDecimal amount, 
                            Integer transactionType, String relationId, String remark);

    /**
     * 更新交易状态
     *
     * @param transactionNo 交易流水号
     * @param status 状态
     * @param beforeBalance 交易前余额
     * @param afterBalance 交易后余额
     */
    void updateTransactionStatus(String transactionNo, Integer status,
                                 BigDecimal beforeBalance, BigDecimal afterBalance);

    /**
     * 查询用户交易记录
     *
     * @param page 页码
     * @param size 每页数量
     * @return 交易记录列表
     */
    List<WalletTransactionVO> getTransactionList(Integer page, Integer size);
}

