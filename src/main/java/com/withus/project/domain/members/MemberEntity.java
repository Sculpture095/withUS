package com.withus.project.domain.members;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "member"
        , uniqueConstraints = @UniqueConstraint(name = "uk_member_id", columnNames = "id"))
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true) //명시적으로 포함된 필드만 사용
public class MemberEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_idx")
    @EqualsAndHashCode.Include
    private Integer memberIdx; //회원번호

    @Column(name = "id", nullable = false, unique = true)
    private String id; //회원아이디

    @Column(name = "password", nullable = false)
    private String password; //회원 비밀번호

    @Column(name = "nickname", nullable = false)
    private String nickname; //회원 닉네임(게시판용)

    @Column(name = "email", nullable = false)
    private String email; //회원 이메일

    @Enumerated(EnumType.STRING) // 또는 EnumType.ORDINAL
    @Column(name = "pcatype_idx", nullable = false)
    private PcaType pcaType; //회원 유형(일반, 파트너, 관리자) (외래키)

    @Enumerated(EnumType.STRING) // Enum 매핑
    @Column(name = "usertype_idx", nullable = false)
    private UserType userType; //회원 유형(개인, 팀, 개인사업자, 법인사업자) (외래키)

    @Enumerated(EnumType.STRING) // Enum 매핑
    @Column(name = "rank_idx", nullable = false)
    private RankType rankType = RankType.BASIC; //회원등급(일반, 플러스, 프라임) (외래키)

    @Column(name = "name", nullable = false)
    private String name; // 회원 이름

    @Column(name = "phone", nullable = false, length = 11)
    private String phone; // 회원 전화번호

    @OneToOne(mappedBy = "member", cascade = {}, orphanRemoval = true)
    private ClientEntity client;

    @OneToOne(mappedBy = "member", cascade = {}, orphanRemoval = true)
    private PartnerEntity partner;

    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL)
    private MyPageEntity myPage;






    public MemberEntity(Integer memberIdx, String email, String id, String nickname, String password, String phone) {
        this.memberIdx = memberIdx;
        this.email = email;
        this.id = id;
        this.nickname = nickname;
        this.password = password;
        this.phone = phone;
    }





}



