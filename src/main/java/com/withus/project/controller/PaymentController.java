package com.withus.project.controller;

import com.withus.project.dto.projects.PaymentDTO;
import com.withus.project.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/complete")
    public ResponseEntity<PaymentDTO> completePayment(@RequestBody PaymentDTO paymentDTO) {
        try {
            log.info("PaymentDTO received: {}", paymentDTO);

            // 결제 검증 + DB 저장
            PaymentDTO saved = paymentService.verifyAndSavePayment(paymentDTO);
            if (saved == null) {
                // 결제가 실패/취소 or 검증 불일치
                return ResponseEntity.ok(null);
            }
            // 정상 paid
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            log.error("Payment error: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(null);
        }
    }
}
