package com.withus.project.domain.members;

import com.withus.project.config.UUIDToStringConverter;
import com.withus.project.domain.projects.CaseEntity;
import com.withus.project.domain.projects.ProjectEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "selectskill",
        uniqueConstraints = @UniqueConstraint(name = "uk_selectskill_id", columnNames = "selectskill_id"))
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SelectSkillEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "selectskill_idx")
    private Integer selectSkillIdx;

    @Column(name = "selectskill_id", columnDefinition = "CHAR(36)", unique = true, nullable = false, updatable = false)
    @Convert(converter = UUIDToStringConverter.class)
    private UUID selectSkillId;


    // Getter 수동 추가
    @Getter
    @Enumerated(EnumType.STRING)
    @Column(name = "skill_code", nullable = false)
    private SkillType skillType; // 기술 Enum

    // 🟢 프로젝트에서 사용되는 기술 (프로젝트에만 속함)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_idx", foreignKey = @ForeignKey(name = "fk_selectskill_project"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private ProjectEntity project;

    // 🟢 개발자(파트너스)가 보유한 기술 (개발자에게만 속함)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partner_idx", foreignKey = @ForeignKey(name = "fk_selectskill_partner"))
    private PartnerEntity partner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_idx", foreignKey = @ForeignKey(name = "fk_selectskill_case"))
    private CaseEntity caseEntity;

    @Column(name = "career_duration")
    private Integer careerDuration; // 기술 경력 기간

    @PrePersist
    public void prePersist() {
        if (this.selectSkillId == null){
            this.selectSkillId = UUID.randomUUID();
        }
    }

    
}
