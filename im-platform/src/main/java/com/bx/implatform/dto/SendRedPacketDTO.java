package com.bx.implatform.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 发送红包DTO
 *
 * @author blue
 * @since 2025-10-06
 */
@Data
@Schema(description = "发送红包DTO")
public class SendRedPacketDTO {

    @NotNull(message = "红包类型不能为空")
    @Min(value = 1, message = "红包类型取值范围:1-普通红包,2-拼手气红包")
    @Max(value = 2, message = "红包类型取值范围:1-普通红包,2-拼手气红包")
    @Schema(description = "红包类型:1-普通红包,2-拼手气红包")
    private Integer packetType;

    @NotNull(message = "总金额不能为空")
    @DecimalMin(value = "0.01", message = "红包金额最小为0.01元")
    @DecimalMax(value = "200.00", message = "红包金额最大为200元")
    @Schema(description = "总金额")
    private BigDecimal totalAmount;

    @NotNull(message = "红包个数不能为空")
    @Min(value = 1, message = "红包个数最小为1个")
    @Max(value = 100, message = "红包个数最大为100个")
    @Schema(description = "红包个数")
    private Integer totalCount;

    @NotNull(message = "聊天类型不能为空")
    @Min(value = 1, message = "聊天类型取值范围:1-单聊,2-群聊")
    @Max(value = 2, message = "聊天类型取值范围:1-单聊,2-群聊")
    @Schema(description = "聊天类型:1-单聊,2-群聊")
    private Integer chatType;

    @NotNull(message = "目标ID不能为空")
    @Schema(description = "目标ID(用户ID或群ID)")
    private Long targetId;

    @Schema(description = "祝福语")
    private String message;

    @NotEmpty(message = "支付密码不能为空")
    @Pattern(regexp = "^\\d{6}$", message = "支付密码必须为6位数字")
    @Schema(description = "支付密码")
    private String payPassword;
}

