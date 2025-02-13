package com.withus.project.domain.members;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.withus.project.config.UUIDToStringConverter;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
//@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Entity
@Table(
        name = "partner",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_partner_id", columnNames = "partner_id"),
                @UniqueConstraint(name = "uk_partner_member_idx", columnNames = "member_idx")
        }
)
//@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class PartnerEntity  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "partner_idx")
    private Integer partnerIdx; //파트너스 번호 기본키


    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "member_idx", nullable = false, unique = true,foreignKey = @ForeignKey(name = "fk_partner_member"))
    private MemberEntity member; // 부모의 member_idx를 외래 키로 참조

    @OneToMany(mappedBy = "partner", cascade = CascadeType.ALL)
    private List<PortfolioEntity> portfolios; // PartnerEntity에서 PortfolioEntity 역참조


    @Column(name = "degree")
    private String degree; // 최종학력

    @Column(name = "schoolname")
    private String schoolName; // 학교명

    @Column(name = "major")
    private String major; // 전공

    @Column(name = "admission")
    private String admission; //입학년도

    @Column(name = "graduation")
    private String graduation; //졸업년도

    @Column(name = "location")
    private String location; //거주지역


    // 🟢 개발자가 보유한 기술들 (OneToMany)
    @OneToMany(mappedBy = "partner", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SelectSkillEntity> ownedSkills;




    public PartnerEntity() {
    }

//    public PartnerEntity(Integer memberIdx, String id, String password, String nickname, String email, Integer pca, Integer phone,
//                         Integer partnerIdx, String degree, String schoolName, String major, String admission, String graduation, String location) {
//        super(memberIdx, id, password, nickname, email, pca, phone); // 부모 클래스 필드 초기화
//        this.partnerIdx = partnerIdx;
//        this.degree = degree;
//        this.schoolName = schoolName;
//        this.major = major;
//        this.admission = admission;
//        this.graduation = graduation;
//        this.location = location;
//    }
public PartnerEntity(Integer partnerIdx, MemberEntity member, String degree, String schoolName, String major,
                     String admission, String graduation, String location) {
    this.partnerIdx = partnerIdx;
    this.member = member;
    this.degree = degree;
    this.schoolName = schoolName;
    this.major = major;
    this.admission = admission;
    this.graduation = graduation;
    this.location = location;
}


}
