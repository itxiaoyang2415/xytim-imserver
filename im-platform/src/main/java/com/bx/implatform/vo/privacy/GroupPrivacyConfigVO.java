package com.bx.implatform.vo.privacy;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 群组隐私配置VO
 *
 * @author blue
 * @since 2025-10-06
 */
@Data
@Schema(description = "群组隐私配置VO")
public class GroupPrivacyConfigVO {

    @Schema(description = "配置ID")
    private Long id;

    @Schema(description = "群组ID")
    private Long groupId;

    @Schema(description = "隐私保护是否开启: 0-关闭, 1-开启")
    private Integer privacyEnabled;

    @Schema(description = "管理员是否可见真实信息: 0-否, 1-是")
    private Integer adminViewReal;

    @Schema(description = "创建人ID")
    private Long createdBy;

    @Schema(description = "创建时间")
    private Date createdAt;

    @Schema(description = "更新时间")
    private Date updatedAt;
}

