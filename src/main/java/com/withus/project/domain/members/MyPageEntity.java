package com.withus.project.domain.members;

import com.withus.project.config.UUIDToStringConverter;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "mypage",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_mypage_id", columnNames = "mypage_id"),
                @UniqueConstraint(name = "uk_mypage_member_idx", columnNames = "member_idx")
        })
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MyPageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mypage_idx")
    private Integer myPageIdx;

    @Column(name = "mypage_id", columnDefinition = "CHAR(36)", unique = true, nullable = false, updatable = false)
    @Convert(converter = UUIDToStringConverter.class)
    private UUID myPageId;

    @OneToOne
    @JoinColumn(name = "member_idx", nullable = false,foreignKey = @ForeignKey(name = "fk_mypage_member"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private MemberEntity member; // MemberEntity를 참조하는 외래키

    @Column(name = "address")
    private String address; // 주소

    @Column(name = "profile")
    private String profile; // 프로필 사진

    @Column(name = "account")
    private String account; // 계좌번호




    @Column(name = "introduce", length = 1000)
    private String introduce; // 자기소개

    @Column(name = "zipcode")
    private String zipcode; // 우편번호

    @Column(name = "businessnum")
    private Integer businessNum; // 사업자번호

    @Column(name = "bankname")
    private String bankName;

    @PrePersist
    public void prePersist() {
        if (this.myPageId == null){
            this.myPageId = UUID.randomUUID();
        }
    }



}
