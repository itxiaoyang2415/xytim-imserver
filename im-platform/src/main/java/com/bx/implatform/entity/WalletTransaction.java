package com.bx.implatform.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 交易流水实体类
 *
 * @author blue
 * @since 2025-10-06
 */
@Data
@TableName("t_wallet_transaction")
public class WalletTransaction {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 交易流水号
     */
    private String transactionNo;

    /**
     * 钱包ID
     */
    private Long walletId;

    /**
     * 付款用户ID
     */
    private Long fromUserId;

    /**
     * 收款用户ID
     */
    private Long toUserId;

    /**
     * 交易金额
     */
    private BigDecimal amount;

    /**
     * 交易类型:1-转账,2-红包,3-充值,4-提现,5-退款
     */
    private Integer transactionType;

    /**
     * 业务类型
     */
    private String businessType;

    /**
     * 状态:1-处理中,2-成功,3-失败,4-已退款
     */
    private Integer status;

    /**
     * 交易备注
     */
    private String remark;

    /**
     * 关联业务ID(如红包ID、订单ID)
     */
    private String relationId;

    /**
     * 手续费
     */
    private BigDecimal fee;

    /**
     * 交易前余额
     */
    private BigDecimal beforeBalance;

    /**
     * 交易后余额
     */
    private BigDecimal afterBalance;

    /**
     * 创建时间
     */
    private Date createdAt;

    /**
     * 更新时间
     */
    private Date updatedAt;
}

