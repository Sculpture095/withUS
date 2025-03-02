package com.withus.project.domain.projects;

import com.withus.project.domain.members.SkillType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProjectProgressStatus {


    WAITING_PAYMENT(0,"결제대기"),
    COMPLETE_PAYMENT(1,"결제완료");

    private final int code;
    private final String description;

    public static ProjectProgressStatus fromCode(int code){
        for(ProjectProgressStatus status : ProjectProgressStatus.values()){
            if(status.getCode() == code){
                return status;
            }
        }
            throw new IllegalArgumentException("Invalid code for ProjectProgress: " + code);
    }

    public static ProjectProgressStatus fromName(String name) {
        for (ProjectProgressStatus progressStatus : values()) {
            if (progressStatus.name().equalsIgnoreCase(name)) {
                return progressStatus;
            }
        }
        throw new IllegalArgumentException("Invalid progressStatus: " + name);
    }



}
