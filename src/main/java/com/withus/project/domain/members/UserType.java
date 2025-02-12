package com.withus.project.domain.members;

import lombok.Getter;

@Getter
public enum UserType {
    INDIVIDUAL(0, "개인"),
    TEAM(1, "팀"),
    SOLE_PROPRIETOR(2, "개인사업자"),
    CORPORATION(3,"법인사업자");

    private final int code;
    private final String description;

    UserType(int code, String description) {
        this.code = code;
        this.description = description;
    }
    public static UserType fromCode(int code) {
        for (UserType type : UserType.values()) {
            if (type.getCode() == code) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid code for UserType: " + code);
    }

}
