package com.withus.project.domain.members;

import com.withus.project.config.UUIDToStringConverter;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@ToString
@Table(name = "certificate",
        uniqueConstraints = @UniqueConstraint(name = "uk_certificate_id", columnNames = "certificate_id"))
public class CertificateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "certificate_idx")
    private Integer certificateIdx;

    @Column(name = "certificate_id", columnDefinition = "CHAR(36)", unique = true, nullable = false, updatable = false)
    @Convert(converter = UUIDToStringConverter.class)
    private UUID certificateId;

    @ManyToOne
    @JoinColumn(name = "partner_idx", nullable = false,foreignKey = @ForeignKey(name = "fk_certificate_partner"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private PartnerEntity partner; // PartnerEntity와의 연관 관계

    @Column(name = "certificatename", nullable = false, length = 100)
    private String certificateName; // 자격증 이름

    @Column(name = "certificatedate", length = 10)
    private String certificateDate; // 취득일

    @Column(name = "institutionalname", nullable = false, length = 100)
    private String institutionalName; // 발급기관 이름

    @PrePersist
    public void prePersist() {
        if (this.certificateId == null){
            this.certificateId = UUID.randomUUID();
        }
    }

}
