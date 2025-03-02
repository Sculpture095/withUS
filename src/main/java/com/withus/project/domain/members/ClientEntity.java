package com.withus.project.domain.members;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.withus.project.config.UUIDToStringConverter;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.UUID;

@Getter
@Setter
//@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(
        name = "client",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_client_id", columnNames = "client_id"),
                @UniqueConstraint(name = "uk_client_member_idx", columnNames = "member_idx")
        }
)

public class ClientEntity  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "client_idx")
    private Integer clientIdx; //클라이언트 번호




    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "member_idx", nullable = false,foreignKey = @ForeignKey(name = "fk_client_member"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private MemberEntity member; // 부모의 member_idx를 외래 키로 참조


    public ClientEntity() {
    }

public ClientEntity(Integer clientIdx, MemberEntity member) {
    this.clientIdx = clientIdx;
    this.member = member;

}


    }
