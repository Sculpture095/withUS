package com.withus.project.domain.projects;

import com.withus.project.config.UUIDToStringConverter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name="ucansign_webhook",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_ucansign_webhook_id", columnNames = "ucansign_webhook_id")
        })
public class UCanSignWebhookEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ucansign_webhook_idx")
    private Integer ucanSignWebhookIdx;

    @Column(name = "ucansign_webhook_id", columnDefinition = "CHAR(36)", unique = true, nullable = false, updatable = false)
    @Convert(converter = UUIDToStringConverter.class)
    private UUID ucanSignWebhookId;

    @Column(name = "document_id", length = 100, nullable = false)
    private String documentId;

    @Column(name="event_type", length = 50, nullable = false)
    private String eventType;

    @Column(columnDefinition = "TEXT", name = "payload", nullable = false)
    private String payload;

    @Column(name = "created_at", nullable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "contract_idx", nullable = false, foreignKey = @ForeignKey(name = "fk_case_contract"))
    private ContractEntity contract; // ContractEntity를 참조하는 외래키

    @PrePersist
    public void prePersist() {
        if (this.ucanSignWebhookId == null){
            this.ucanSignWebhookId = UUID.randomUUID();
        }
    }

}
