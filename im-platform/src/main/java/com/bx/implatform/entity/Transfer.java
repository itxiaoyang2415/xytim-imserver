package com.bx.implatform.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 转账实体
 *
 * @author blue
 * @since 2025-10-07
 */
@Data
@TableName("t_transfer")
public class Transfer {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 转账编号
     */
    private String transferNo;

    /**
     * 转账方用户ID
     */
    private Long fromUserId;

    /**
     * 收款方用户ID
     */
    private Long toUserId;

    /**
     * 转账金额
     */
    private BigDecimal amount;

    /**
     * 状态:1-待领取,2-已领取,3-已过期,4-已退款
     */
    private Integer status;

    /**
     * 转账备注
     */
    private String remark;

    /**
     * 过期时间(24小时)
     */
    private Date expireTime;

    /**
     * 领取时间
     */
    private Date receiveTime;

    /**
     * 退款时间
     */
    private Date refundTime;

    /**
     * 关联的交易流水号
     */
    private String transactionNo;

    /**
     * 创建时间
     */
    private Date createdAt;

    /**
     * 更新时间
     */
    private Date updatedAt;
}

