package com.withus.project.dto.projects;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
public class PaymentDTO {
    private Long paymentIdx; // 결제 ID 기본키 (요청용)
    private String contractId; // 계약 ID (요청용)
    private String impUid; //결제 고유번호
    private String merchantUid; //가맹점에서 생성한 주문번호
    private String payMethod;     //결제수단
    private String status; //결제 상태
    private BigDecimal amount; // 결제 금액 (요청/응답 공용)
    private LocalDateTime paidAt; //결제시각
    private LocalDateTime cancelledAt; //결제 취소시각
    private BigDecimal cancelAmount; //취소금액
    private String failReason; //실패사유

}
