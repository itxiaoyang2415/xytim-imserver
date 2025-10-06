package com.bx.implatform.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 红包类型枚举
 *
 * @author blue
 * @since 2025-10-06
 */
@Getter
@AllArgsConstructor
public enum RedPacketTypeEnum {
    NORMAL(1, "普通红包"),
    LUCKY(2, "拼手气红包");

    private final Integer code;
    private final String desc;

    public static RedPacketTypeEnum fromCode(Integer code) {
        for (RedPacketTypeEnum typeEnum : values()) {
            if (typeEnum.code.equals(code)) {
                return typeEnum;
            }
        }
        return null;
    }
}

