package com.withus.project.domain.projects;

import lombok.Getter;

@Getter
public enum PaymentMethod {
    CARD(0,"카드"), //카드
    BANK_TRANSFER (1,"계좌이체");//계좌이체

    private final int code;
    private final String description;

    PaymentMethod(int code, String description) {
        this.code = code;
        this.description = description;
    }
    public static PaymentMethod fromCode(int code) {
        for (PaymentMethod method : PaymentMethod.values()) {
            if (method.getCode() == code) {
                return method;
            }
        }
        throw new IllegalArgumentException("Invalid code for PaymentMethod: " + code);
    }
}
