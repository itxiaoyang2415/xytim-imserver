package com.bx.implatform.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 交易流水VO
 *
 * @author blue
 * @since 2025-10-06
 */
@Data
@Schema(description = "交易流水VO")
public class WalletTransactionVO {

    @Schema(description = "流水ID")
    private Long id;

    @Schema(description = "交易流水号")
    private String transactionNo;

    @Schema(description = "付款用户ID")
    private Long fromUserId;

    @Schema(description = "付款用户昵称")
    private String fromUserName;

    @Schema(description = "收款用户ID")
    private Long toUserId;

    @Schema(description = "收款用户昵称")
    private String toUserName;

    @Schema(description = "交易金额")
    private BigDecimal amount;

    @Schema(description = "交易类型:1-转账,2-红包,3-充值,4-提现,5-退款")
    private Integer transactionType;

    @Schema(description = "交易类型描述")
    private String transactionTypeDesc;

    @Schema(description = "业务类型")
    private String businessType;

    @Schema(description = "状态:1-处理中,2-成功,3-失败,4-已退款")
    private Integer status;

    @Schema(description = "状态描述")
    private String statusDesc;

    @Schema(description = "交易备注")
    private String remark;

    @Schema(description = "关联业务ID")
    private String relationId;

    @Schema(description = "手续费")
    private BigDecimal fee;

    @Schema(description = "交易前余额")
    private BigDecimal beforeBalance;

    @Schema(description = "交易后余额")
    private BigDecimal afterBalance;

    @Schema(description = "创建时间")
    private Date createdAt;

    @Schema(description = "更新时间")
    private Date updatedAt;
}

