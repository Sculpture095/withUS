package com.withus.project.domain.projects;

import lombok.Getter;

@Getter
public enum Construction {
    PERIOD(0,"기간제"),
    OUTSOURCE(1,"외주");

    private final int code;
    private final String description;

    Construction(int code, String description) {
        this.code = code;
        this.description = description;
    }
    public static Construction fromCode(int code) {
        for (Construction type : Construction.values()) {
            if (type.code == code) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid code: " + code);
    }


}
