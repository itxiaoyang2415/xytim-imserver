package com.bx.implatform.vo;

import com.bx.imcommon.serializer.DateToLongSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 转账结果VO
 *
 * @author blue
 * @since 2025-10-06
 */
@Data
@Schema(description = "转账结果VO")
public class TransferVO {

    @Schema(description = "交易流水号")
    private String transactionNo;

    @Schema(description = "付款用户ID")
    private Long fromUserId;

    @Schema(description = "收款用户ID")
    private Long toUserId;

    @Schema(description = "转账金额")
    private BigDecimal amount;

    @Schema(description = "交易前余额")
    private BigDecimal beforeBalance;

    @Schema(description = "交易后余额")
    private BigDecimal afterBalance;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "转账时间")
    @JsonSerialize(using = DateToLongSerializer.class)
    private Date transferTime;
}

