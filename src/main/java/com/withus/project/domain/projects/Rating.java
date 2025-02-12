package com.withus.project.domain.projects;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
@Getter
@RequiredArgsConstructor
public enum Rating {
    EXCELLENT(5,"★★★★★"), //우수
    GOOD(4,"★★★★"), //좋음
    AVERAGE(3,"★★★"), //보통
    POOR(2,"★★"), //나쁨
    TERRIBLE(1,"★"); //매우 나쁨

    private final int code;
    private final String description;

    public static Rating fromCode(int code) {
        for (Rating rating : Rating.values()) {
            if (rating.getCode() == code) {
                return rating;
            }
        }
        throw new IllegalArgumentException("Invalid code for Rating: " + code);
    }

}
