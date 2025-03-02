package com.withus.project.domain.projects;

import com.withus.project.config.UUIDToStringConverter;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private Long paymentIdx;

    @ManyToOne
    @JoinColumn(name = "contract_idx", nullable = false,foreignKey = @ForeignKey(name = "fk_payment_contract"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private ContractEntity contract; // ContractEntity를 참조하는 외래키

    //아임포트에서 부여하는 결제 고유번호
    @Column(name = "imp_uid", length = 50)
    private String impUid;

    //가맹점에서 생성한 주문번호
    @Column(name = "merchant_uid", length = 50)
    private String merchantUid;

    //결제수단
    @Column(name = "pay_method", length = 20)
    private String payMethod;

    @Column(name = "status", length = 20)
    private String status;


    @Column(name = "amount", precision = 15, scale = 2)
    private BigDecimal amount; // 결제 금액

    //결제시각
    private LocalDateTime paidAt;

    private LocalDateTime cancelledAt;

    // (8) 취소 금액 (부분취소 등을 위해)
    @Column(precision = 15, scale = 2)
    private BigDecimal cancelAmount;

    // (9) 실패 사유 (결제 실패 시)
    @Column(length = 200)
    private String failReason;






}
