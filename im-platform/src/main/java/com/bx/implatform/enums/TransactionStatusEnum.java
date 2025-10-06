package com.bx.implatform.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 交易状态枚举
 *
 * @author blue
 * @since 2025-10-06
 */
@Getter
@AllArgsConstructor
public enum TransactionStatusEnum {
    PROCESSING(1, "处理中"),
    SUCCESS(2, "成功"),
    FAILED(3, "失败"),
    REFUNDED(4, "已退款");

    private final Integer code;
    private final String desc;

    public static TransactionStatusEnum fromCode(Integer code) {
        for (TransactionStatusEnum statusEnum : values()) {
            if (statusEnum.code.equals(code)) {
                return statusEnum;
            }
        }
        return null;
    }
}

