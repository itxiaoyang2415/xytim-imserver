package com.bx.implatform.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 领取转账DTO
 *
 * @author blue
 * @since 2025-10-07
 */
@Data
@Schema(description = "领取转账DTO")
public class ReceiveTransferDTO {

    @NotBlank(message = "转账编号不能为空")
    @Schema(description = "转账编号", required = true)
    private String transferNo;
}

