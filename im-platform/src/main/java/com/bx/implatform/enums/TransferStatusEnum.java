package com.bx.implatform.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 转账状态枚举
 *
 * @author blue
 * @since 2025-10-07
 */
@Getter
@AllArgsConstructor
public enum TransferStatusEnum {

    /**
     * 待领取
     */
    PENDING(1, "待领取"),

    /**
     * 已领取
     */
    RECEIVED(2, "已领取"),

    /**
     * 已过期
     */
    EXPIRED(3, "已过期"),

    /**
     * 已退款
     */
    REFUNDED(4, "已退款");

    private final Integer code;
    private final String desc;

    public static TransferStatusEnum fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (TransferStatusEnum statusEnum : TransferStatusEnum.values()) {
            if (statusEnum.getCode().equals(code)) {
                return statusEnum;
            }
        }
        return null;
    }
}

