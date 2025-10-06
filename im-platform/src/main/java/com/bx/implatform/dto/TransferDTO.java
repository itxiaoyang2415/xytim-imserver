package com.bx.implatform.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 转账DTO
 *
 * @author blue
 * @since 2025-10-06
 */
@Data
@Schema(description = "转账DTO")
public class TransferDTO {

    @NotNull(message = "收款用户ID不能为空")
    @Schema(description = "收款用户ID")
    private Long toUserId;

    @NotNull(message = "转账金额不能为空")
    @DecimalMin(value = "0.01", message = "转账金额最小为0.01元")
    @DecimalMax(value = "50000.00", message = "转账金额最大为50000元")
    @Schema(description = "转账金额")
    private BigDecimal amount;

    @Schema(description = "转账备注")
    private String remark;

    @NotEmpty(message = "支付密码不能为空")
    @Pattern(regexp = "^\\d{6}$", message = "支付密码必须为6位数字")
    @Schema(description = "支付密码")
    private String payPassword;
}

