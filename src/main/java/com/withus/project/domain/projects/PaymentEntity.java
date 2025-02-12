package com.withus.project.domain.projects;

import com.withus.project.config.UUIDToStringConverter;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "payment",
        uniqueConstraints = @UniqueConstraint(name = "uk_payment_id", columnNames = "payment_id"))
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PaymentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_idx")
    private Integer paymentIdx;

    @Column(name = "payment_id", columnDefinition = "CHAR(36)", unique = true, nullable = false, updatable = false)
    @Convert(converter = UUIDToStringConverter.class)
    private UUID paymentId;

    @ManyToOne
    @JoinColumn(name = "contract_idx", nullable = false,foreignKey = @ForeignKey(name = "fk_payment_contract"))
    private ContractEntity contract; // ContractEntity를 참조하는 외래키

    @Column(name = "amount", nullable = false)
    private Double amount; // 결제 금액

    @Column(name = "fee", nullable = false)
    private Double fee; // 수수료

    @Enumerated(EnumType.STRING)
    @Column(name = "method", nullable = false)
    private PaymentMethod method; // 결제 방법

    @Column(name = "account", nullable = false)
    private String account; // 결제 계좌

    @PrePersist
    public void prePersist() {
        if (this.paymentId == null){
            this.paymentId = UUID.randomUUID();
        }
    }


}
