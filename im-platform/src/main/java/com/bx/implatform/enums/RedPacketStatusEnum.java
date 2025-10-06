package com.bx.implatform.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 红包状态枚举
 *
 * @author blue
 * @since 2025-10-06
 */
@Getter
@AllArgsConstructor
public enum RedPacketStatusEnum {
    CREATED(1, "已创建"),
    SENDING(2, "发放中"),
    FINISHED(3, "已领完"),
    EXPIRED(4, "已过期"),
    REFUNDED(5, "已退款");

    private final Integer code;
    private final String desc;

    public static RedPacketStatusEnum fromCode(Integer code) {
        for (RedPacketStatusEnum statusEnum : values()) {
            if (statusEnum.code.equals(code)) {
                return statusEnum;
            }
        }
        return null;
    }
}

