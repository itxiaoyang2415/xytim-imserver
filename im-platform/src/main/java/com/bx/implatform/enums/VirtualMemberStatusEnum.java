package com.bx.implatform.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 虚拟成员状态枚举
 *
 * @author blue
 * @since 2025-10-06
 */
@Getter
@AllArgsConstructor
public enum VirtualMemberStatusEnum {
    NORMAL(1, "正常"),
    INVALID(2, "已失效");

    private final Integer code;
    private final String desc;

    public static VirtualMemberStatusEnum fromCode(Integer code) {
        for (VirtualMemberStatusEnum statusEnum : values()) {
            if (statusEnum.code.equals(code)) {
                return statusEnum;
            }
        }
        return null;
    }
}

