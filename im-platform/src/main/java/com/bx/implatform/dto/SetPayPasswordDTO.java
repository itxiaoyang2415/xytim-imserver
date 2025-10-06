package com.bx.implatform.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 设置支付密码DTO
 *
 * @author blue
 * @since 2025-10-06
 */
@Data
@Schema(description = "设置支付密码DTO")
public class SetPayPasswordDTO {

    @NotEmpty(message = "支付密码不能为空")
    @Pattern(regexp = "^\\d{6}$", message = "支付密码必须为6位数字")
    @Schema(description = "支付密码（6位数字）")
    private String payPassword;

    @NotEmpty(message = "确认支付密码不能为空")
    @Pattern(regexp = "^\\d{6}$", message = "确认支付密码必须为6位数字")
    @Schema(description = "确认支付密码（6位数字）")
    private String confirmPayPassword;
}

