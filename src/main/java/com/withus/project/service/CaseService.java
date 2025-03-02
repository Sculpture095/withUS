package com.withus.project.service;

import com.withus.project.domain.projects.CaseEntity;
import com.withus.project.domain.projects.ContractEntity;
import com.withus.project.dto.projects.CaseDTO;
import com.withus.project.mapper.projects.CaseMapper;
import com.withus.project.repository.projects.CaseRepositoryImpl;
import com.withus.project.repository.projects.ContractRepositoryImpl;
import jakarta.persistence.NoResultException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CaseService {


    private final CaseRepositoryImpl caseRepository;
    private final ContractRepositoryImpl contractRepository;
    private final CaseMapper caseMapper;

    @Transactional
    public CaseDTO createCase(CaseDTO dto) {
        // 1) DTO → Entity
        CaseEntity entity = caseMapper.toEntity(dto);

        // 2) contractId(UUID) 조회
        String c = dto.getContractId();
        if (c == null) {
            throw new IllegalArgumentException("contractId가 null입니다.");
        }
        UUID contractUuid = UUID.fromString(c);
        ContractEntity realContract = contractRepository.findByContractId(contractUuid);
        if (realContract == null) {
            throw new IllegalArgumentException("존재하지 않는 contractId=" + c);
        }

        // 엔티티에 진짜 ContractEntity 세팅
        entity.setContract(realContract);

        // 3) 저장
        caseRepository.save(entity);

        // 4) 다시 DTO로
        return caseMapper.toDTO(entity);
    }

    public CaseDTO getCase(String caseIdString){
        UUID caseId = UUID.fromString(caseIdString);
        CaseEntity entity = caseRepository.findByCaseId(caseId);
        return caseMapper.toDTO(entity);
    }



}
