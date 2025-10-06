package com.bx.implatform.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 充值提现订单实体类
 *
 * @author blue
 * @since 2025-10-06
 */
@Data
@TableName("t_wallet_order")
public class WalletOrder {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 类型:1-充值,2-提现
     */
    private Integer type;

    /**
     * 金额
     */
    private BigDecimal amount;

    /**
     * 支付渠道:alipay,wechat,bank
     */
    private String channel;

    /**
     * 状态:1-处理中,2-成功,3-失败,4-已取消
     */
    private Integer status;

    /**
     * 渠道订单号
     */
    private String channelOrderNo;

    /**
     * 渠道回调数据
     */
    private String notifyData;

    /**
     * 完成时间
     */
    private Date completedTime;

    /**
     * 创建时间
     */
    private Date createdAt;

    /**
     * 更新时间
     */
    private Date updatedAt;
}

