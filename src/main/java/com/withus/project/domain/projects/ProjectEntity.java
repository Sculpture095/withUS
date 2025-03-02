package com.withus.project.domain.projects;

import com.withus.project.config.UUIDToStringConverter;
import com.withus.project.domain.members.ClientEntity;
import com.withus.project.domain.members.SelectSkillEntity;
import com.withus.project.domain.members.SkillType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.mapping.ToOne;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "project",
        uniqueConstraints = @UniqueConstraint(name = "uk_project_id", columnNames = "project_id"))
@NoArgsConstructor
@AllArgsConstructor
public class ProjectEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_idx")
    private Integer projectIdx;

    @Column(name = "project_id", columnDefinition = "CHAR(36)", unique = true, nullable = false, updatable = false)
    @Convert(converter = UUIDToStringConverter.class)
    private UUID projectId;

    @ManyToOne
    @JoinColumn(name = "client_idx", nullable = false,foreignKey = @ForeignKey(name = "fk_project_client"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private ClientEntity client; // ClientEntity를 참조하는 외래키


    @Column(name = "projectname", length = 70, nullable = false)
    private String projectName; // 프로젝트 이름

    @Enumerated(EnumType.STRING)
    @Column(name = "prostatement", nullable = false)
    private ProjectStatus proStatement = ProjectStatus.ON_GOING; // 프로젝트 상태 기본:모집중

    @Column(name = "amount", nullable = false)
    private Double amount; // 금액

    @Column(name = "duration", length = 10, nullable = false)
    private String duration; // 기간

    @Column(name = "startdate", nullable = false)
    private LocalDate startDate; // 시작일자

    @Enumerated(EnumType.STRING) // Enum을 문자열로 저장
    @Column(name = "construction", nullable = false)
    private Construction construction;


    @Column(name = "projectlocation", length = 20)
    private String projectLocation; // 프로젝트 위치

    @Column(name = "registrationdate", nullable = false,updatable = false)
    private LocalDate registrationDate = LocalDate.now(); //등록일자

    @Column(name = "teamsize", nullable = false, length = 3)
    private Integer teamSize; // 모집 인원

    @Column(name = "projectinfo", length = 5000, nullable = false)
    private String projectInfo; // 프로젝트 소개

    @Column(name = "is_completed", nullable = false, columnDefinition = "TINYINT(1) DEFAULT 0")
    private Boolean isCompleted; //프로젝트 완료여부 (0: 미완료, 1: 완료)

    @Column(name = "closingdate", nullable = false)
    private LocalDate closingDate; // 프로젝트 마감일

    @Enumerated(EnumType.STRING)
    @Column(name = "progressStatus", nullable = false)
    private ProjectProgressStatus progressStatus = ProjectProgressStatus.WAITING_PAYMENT;

    // 🟢 프로젝트에서 선택한 기술들 (OneToMany)
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<SelectSkillEntity> selectedSkills;

    // ✅ Enum의 description을 바로 반환하는 메서드 추가
    @Transient
    public String getProStatementDescription() {
        return (proStatement != null) ? proStatement.getDescription() : "";
    }
    @Transient
    public String getConstructionDescription() {
        return (construction != null) ? construction.getDescription() : "";
    }


    @PrePersist
    public void prePersist() {
        if (this.projectId == null){
            this.projectId = UUID.randomUUID();
        }
        if (this.isCompleted == null) {
            this.isCompleted = false;
        }
    }


}
