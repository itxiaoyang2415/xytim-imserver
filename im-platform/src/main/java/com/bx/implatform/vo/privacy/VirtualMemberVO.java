package com.bx.implatform.vo.privacy;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 虚拟成员VO
 *
 * @author blue
 * @since 2025-10-06
 */
@Data
@Schema(description = "虚拟成员VO")
public class VirtualMemberVO {

    @Schema(description = "真实用户ID")
    private Long realUserId;

    @Schema(description = "虚拟用户ID")
    private Long virtualUserId;

    @Schema(description = "虚拟昵称")
    private String virtualNickName;

    @Schema(description = "显示序号")
    private Integer displayOrder;
}

