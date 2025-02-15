package com.withus.project.domain.dto.members;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class MyPageDTO {
    private Integer myPageIdx; // 마이페이지 ID 기본키 (요청용)
    private String myPageId; // UUID 기본키 (응답용)
    private Integer memberIdx; // 회원 ID (요청용)
    private String address; // 주소
    private String profile; // 프로필 사진
    private String account; // 계좌번호
    private String introduce; // 자기소개
    private String zipcode; // 우편번호

    private Integer businessNum; // 사업자 번호 (응답용)
    private String bankName;

    public UUID getMyPageIdAsUUID() {
        return myPageId != null ? UUID.fromString(myPageId) : null;
    }
}
