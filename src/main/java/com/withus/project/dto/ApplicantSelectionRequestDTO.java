package com.withus.project.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ApplicantSelectionRequestDTO {

    private String projectId;
    private List<String> applicantIds;
    private String meetingDate;
    private String meetingTime;
}
