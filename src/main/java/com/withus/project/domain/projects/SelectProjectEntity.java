package com.withus.project.domain.projects;

import com.withus.project.config.UUIDToStringConverter;
import com.withus.project.domain.members.PartnerEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "selectproject",
        uniqueConstraints = @UniqueConstraint(name = "uk_selectproject_id", columnNames = "selectproject_id"))
@NoArgsConstructor
@AllArgsConstructor
public class SelectProjectEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "selectproject_idx")
    private Integer selectProjectIdx;

    @Column(name = "selectproject_id", columnDefinition = "CHAR(36)", unique = true, nullable = false, updatable = false)
    @Convert(converter = UUIDToStringConverter.class)
    private UUID selectProjectId;

    @ManyToOne
    @JoinColumn(name = "partner_idx", nullable = false,foreignKey = @ForeignKey(name = "fk_selectproject_partner"))
    private PartnerEntity partner; // 파트너 외래키

    @ManyToOne
    @JoinColumn(name = "project_idx", nullable = false,foreignKey = @ForeignKey(name = "fk_selectproject_project"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private ProjectEntity project; // 프로젝트 외래키

    @Column(name = "yn", nullable = false)
    private Boolean yn = false; // 클라이언트 선택 여부

    @Column(name = "meeting_date")
    private LocalDate meetingDate;

    @Column(name = "meeting_time")
    private LocalTime meetingTime;


    @PrePersist
    public void prePersist() {
        if (this.selectProjectId == null){
            this.selectProjectId = UUID.randomUUID();
        }
    }
}
