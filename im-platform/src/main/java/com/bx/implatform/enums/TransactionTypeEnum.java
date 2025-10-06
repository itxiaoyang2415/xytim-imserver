package com.bx.implatform.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 交易类型枚举
 *
 * @author blue
 * @since 2025-10-06
 */
@Getter
@AllArgsConstructor
public enum TransactionTypeEnum {
    TRANSFER(1, "转账"),
    RED_PACKET(2, "红包"),
    RECHARGE(3, "充值"),
    WITHDRAW(4, "提现"),
    REFUND(5, "退款");

    private final Integer code;
    private final String desc;

    public static TransactionTypeEnum fromCode(Integer code) {
        for (TransactionTypeEnum typeEnum : values()) {
            if (typeEnum.code.equals(code)) {
                return typeEnum;
            }
        }
        return null;
    }
}

