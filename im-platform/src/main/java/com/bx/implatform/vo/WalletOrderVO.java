package com.bx.implatform.vo;

import com.bx.imcommon.serializer.DateToLongSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 充值提现订单VO
 *
 * @author blue
 * @since 2025-10-07
 */
@Data
@Schema(description = "充值提现订单VO")
public class WalletOrderVO {

    @Schema(description = "订单ID")
    private Long id;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "订单号")
    private String orderNo;

    @Schema(description = "类型:1-充值,2-提现")
    private Integer type;

    @Schema(description = "类型描述")
    private String typeDesc;

    @Schema(description = "金额")
    private BigDecimal amount;

    @Schema(description = "支付渠道:alipay,wechat,bank")
    private String channel;

    @Schema(description = "支付渠道描述")
    private String channelDesc;

    @Schema(description = "状态:1-处理中,2-成功,3-失败,4-已取消")
    private Integer status;

    @Schema(description = "状态描述")
    private String statusDesc;

    @Schema(description = "渠道订单号")
    private String channelOrderNo;

    @Schema(description = "完成时间（13位时间戳）")
    @JsonSerialize(using = DateToLongSerializer.class)
    private Date completedTime;

    @Schema(description = "创建时间（13位时间戳）")
    @JsonSerialize(using = DateToLongSerializer.class)
    private Date createdAt;

    @Schema(description = "更新时间（13位时间戳）")
    @JsonSerialize(using = DateToLongSerializer.class)
    private Date updatedAt;
}

