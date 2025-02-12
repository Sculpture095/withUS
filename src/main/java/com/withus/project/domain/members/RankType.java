package com.withus.project.domain.members;

import lombok.Getter;

@Getter
public enum RankType {
    BASIC(0, "일반등급"),
    PLUS(1, "플러스등급"),
    PRIME(2, "프라임등급");

    private final int code;
    private final String description;

    RankType(int code, String description) {
        this.code = code;
        this.description = description;
    }
    public static RankType fromCode(int code) {
        for (RankType rank : RankType.values()) {
            if (rank.getCode() == code) {
                return rank;
            }
        }
        throw new IllegalArgumentException("Invalid code for RankType: " + code);
    }
}
