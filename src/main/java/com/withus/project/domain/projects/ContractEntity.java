package com.withus.project.domain.projects;

import com.withus.project.config.UUIDToStringConverter;
import com.withus.project.domain.members.PartnerEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.mapping.ToOne;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "contract",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_contract_id", columnNames = "contract_id")
        })
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ContractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "contract_idx")
    private Integer contractIdx;

    @Column(name = "contract_id", columnDefinition = "CHAR(36)", unique = true, nullable = false, updatable = false)
    @Convert(converter = UUIDToStringConverter.class)
    private UUID contractId;


    @ManyToOne
    @JoinColumn(name = "partner_idx", referencedColumnName = "partner_idx", nullable = false,foreignKey = @ForeignKey(name = "fk_contract_partner"))
    private PartnerEntity partner;

    @ManyToOne
    @JoinColumn(name = "project_idx", referencedColumnName = "project_idx", nullable = false,foreignKey = @ForeignKey(name = "fk_contract_project"))
    private ProjectEntity project;


    @Column(name = "contractdate", nullable = false)
    private LocalDate contractDate; // 계약일자

    @Enumerated(EnumType.STRING)
    @Column(name = "constatus", nullable = false)
    private ContractStatus status; // 계약 상태

    @Column(name = "amount", nullable = false)
    private Double amount; // 금액

    @Column(name = "detail", length = 200, nullable = false)
    private String detail; // 계약 내용

    @Column(name = "contractname", length = 30, nullable = false)
    private String contractName; // 계약명

    @OneToMany(mappedBy = "contract", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CaseEntity> caseEntities = new ArrayList<>();


    @PrePersist
    public void prePersist() {
        if (this.contractId == null){
            this.contractId = UUID.randomUUID();
        }
    }


}
