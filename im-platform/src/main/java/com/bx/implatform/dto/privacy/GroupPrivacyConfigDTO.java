package com.bx.implatform.dto.privacy;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 群组隐私配置DTO
 *
 * @author blue
 * @since 2025-10-06
 */
@Data
@Schema(description = "群组隐私配置DTO")
public class GroupPrivacyConfigDTO {

    @NotNull(message = "群组ID不能为空")
    @Schema(description = "群组ID")
    private Long groupId;

    @NotNull(message = "隐私保护开关不能为空")
    @Schema(description = "隐私保护是否开启: 0-关闭, 1-开启")
    private Integer privacyEnabled;
}

