package com.bx.implatform.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * @author Blue
 * @version 1.0
 * @date 2025-02-22
 */
@Data
@Schema(description = "修改好友备注")
public class FriendRemarkDTO {

    @NotNull(message = "好友id不可为空")
    @Schema(description = "好友用户id")
    private Long friendId;

    @Length(max = 32, message = "备注名不能大于32字符")
    @Schema(description = "备注名")
    private String remarkNickName;

}
