package com.withus.project.domain.projects;

import lombok.Getter;

@Getter
public enum ProjectStatus {
    COMPLETED(0,"모집완료"), //모집완료
    CANCELED(1,"취소"), //취소
    ON_GOING(2,"모집중");// 모집중

    private final int code;
    private final String description;

    ProjectStatus(int code, String description) {
        this.code = code;
        this.description = description;
    }
    public static ProjectStatus fromCode(int code) {
        for (ProjectStatus status : ProjectStatus.values()) {
            if (status.getCode() == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid code for ProjectStatus: " + code);
    }

}
