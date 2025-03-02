package com.withus.project.service;

import com.withus.project.domain.projects.ContractEntity;
import com.withus.project.domain.projects.PaymentEntity;
import com.withus.project.domain.projects.ProjectEntity;
import com.withus.project.domain.projects.ProjectProgressStatus;
import com.withus.project.dto.projects.PaymentDTO;
import com.withus.project.mapper.projects.PaymentMapper;
import com.withus.project.repository.members.PartnerRepositoryImpl;
import com.withus.project.repository.projects.ContractRepositoryImpl;
import com.withus.project.repository.projects.PaymentRepositoryImpl;
import com.withus.project.repository.projects.ProjectRepositoryImpl;
import com.withus.project.service.other.IamportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final IamportService iamportService;  // v1 전용
    private final PaymentRepositoryImpl paymentRepository;
    private final PaymentMapper paymentMapper;
    private final ContractRepositoryImpl contractRepository;
    private final ProjectRepositoryImpl projectRepository;

    /**
     * 아임포트(v1)로 결제 검증 후 DB 저장
     */
    @Transactional
    public PaymentDTO verifyAndSavePayment(PaymentDTO paymentDTO) {
        // 1) PaymentDTO -> PaymentEntity 변환
        PaymentEntity paymentEntity = paymentMapper.toEntity(paymentDTO);

        // 2) 연결된 ContractEntity 찾기 (UUID 사용)
        ContractEntity contract = contractRepository.findByUUID(paymentDTO.getContractId())
                .orElse(null);

        // 3) Contract 연결 + 임의로 결제상태 "paid" 설정
        paymentEntity.setContract(contract);
        paymentEntity.setStatus("paid");



        // 4) DB 저장
        PaymentEntity saved = paymentRepository.save(paymentEntity);
        if(contract != null && contract.getProject() != null){
            ProjectEntity project = contract.getProject();

            project.setProgressStatus(ProjectProgressStatus.COMPLETE_PAYMENT);
            projectRepository.save(project);
        }

        // 5) 결과 반환
        return paymentMapper.toDTO(saved);
    }
}