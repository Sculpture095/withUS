package com.withus.project.dto.members;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class GoogleLoginDTO {
    private String token;
}
