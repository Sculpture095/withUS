package com.withus.project.domain.members;

import com.withus.project.config.UUIDToStringConverter;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Entity
@ToString
@Table(name = "history",
        uniqueConstraints = @UniqueConstraint(name = "uk_history_id", columnNames = "history_id"))
@NoArgsConstructor
@AllArgsConstructor
public class HistoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_idx")
    private Integer historyIdx;

    @Column(name = "history_id", columnDefinition = "CHAR(36)", unique = true, nullable = false, updatable = false)
    @Convert(converter = UUIDToStringConverter.class)
    private UUID historyId;

    @ManyToOne
    @JoinColumn(name = "partner_idx", nullable = false,foreignKey = @ForeignKey(name = "fk_history_partner"))
    private PartnerEntity partner; // PartnerEntity를 참조하는 외래키

    @Column(name = "duration", length = 20)
    private String duration; // 경력 기간

    @Column(name = "joindate")
    private LocalDate joinDate; // 입사일

    @Column(name = "exitdate")
    private LocalDate exitDate; // 퇴사일

    @Column(name = "companyname", length = 50)
    private String companyName; // 근무 회사명

    @PrePersist
    public void prePersist() {
        if (this.historyId == null){
            this.historyId = UUID.randomUUID();
        }
    }


}
