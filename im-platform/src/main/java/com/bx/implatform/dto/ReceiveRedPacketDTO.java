package com.bx.implatform.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

/**
 * 领取红包DTO
 *
 * @author blue
 * @since 2025-10-06
 */
@Data
@Schema(description = "领取红包DTO")
public class ReceiveRedPacketDTO {

    @NotEmpty(message = "红包编号不能为空")
    @Schema(description = "红包编号")
    private String packetNo;
}

