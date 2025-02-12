package com.withus.project.domain.dto.projects;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
public class PaymentDTO {
    private Integer paymentIdx; // 결제 ID 기본키 (요청용)
    private String paymentId; // 고유식별자 (응답용)
    private String contractId; // 계약 ID (요청용)
    private Double amount; // 결제 금액 (요청/응답 공용)
    private Double fee; // 수수료 (요청/응답 공용)
    private String method; // 결제 방법 (요청/응답 공용)
    private String account; // 결제 계좌 (요청/응답 공용)
}
