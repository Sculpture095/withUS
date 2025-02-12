package com.withus.project.domain.members;

import lombok.Getter;

@Getter
public enum PcaType {
    ADMIN(0,"관리자"),
    PARTNER(1,"파트너"),
    CLIENT(2,"클라이언트");

    private final int code;
    private final String description;

    PcaType(int code, String description) {
        this.code = code;
        this.description = description;
    }

public static PcaType fromCode(int code) {
        for (PcaType type : PcaType.values()) {
            if (type.code == code) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid code: " + code);
    }
}
