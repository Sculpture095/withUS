package com.withus.project.dto.members.clients;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ClientDTO {
    private Integer clientIdx; // 클라이언트 번호 (응답용)
    private Integer memberIdx; // 회원 번호 (요청/응답 공용)



}
