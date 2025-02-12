package com.withus.project.domain.dto.members;

import com.withus.project.domain.members.PcaType;
import com.withus.project.domain.members.RankType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MemberDTO {
    private Integer memberIdx; // 회원 ID (응답용)
    private String id; // 회원 아이디 (요청/응답 공용) (식별)
    private String password; // 회원 비밀번호 (요청용)
    private String nickname; // 회원 닉네임 (요청/응답 공용)
    private String name; // 회원 이름 (요청/응답 공용)
    private String email; // 회원 이메일 (요청/응답 공용)
    private String phone; // 회원 전화번호 (요청/응답 공용)
    private String pcaType; // 회원 유형(일반, 파트너, 관리자) - Enum을 문자열로 처리 (요청/응답 공용)
    private String userType; // 회원 유형(개인, 팀, 개인사업자, 법인사업자) - Enum을 문자열로 처리 (요청/응답 공용)
    private String rankType; // 회원 등급(일반, 플러스, 프라임) - Enum을 문자열로 처리 (요청/응답 공용)
}
