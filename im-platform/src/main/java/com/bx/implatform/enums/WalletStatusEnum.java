package com.bx.implatform.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 钱包状态枚举
 *
 * @author blue
 * @since 2025-10-06
 */
@Getter
@AllArgsConstructor
public enum WalletStatusEnum {
    NORMAL(1, "正常"),
    FROZEN(2, "冻结"),
    DISABLED(3, "禁用");

    private final Integer code;
    private final String desc;

    public static WalletStatusEnum fromCode(Integer code) {
        for (WalletStatusEnum statusEnum : values()) {
            if (statusEnum.code.equals(code)) {
                return statusEnum;
            }
        }
        return null;
    }
}

