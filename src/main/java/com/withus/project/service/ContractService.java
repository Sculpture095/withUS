package com.withus.project.service;

import com.withus.project.dto.projects.ProjectDTO;
import com.withus.project.domain.projects.ContractEntity;
import com.withus.project.domain.projects.ContractStatus;
import com.withus.project.mapper.projects.ProjectMapper;
import com.withus.project.repository.projects.ContractRepositoryImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContractService {

    private final ContractRepositoryImpl contractRepository;
    private final ProjectMapper projectMapper;

    /**
     * 계약 상태가 SIGNED(계약 완료)인 프로젝트 목록을 가져온다.
     * (클라이언트 마이페이지 - "진행중인 프로젝트"를 위해 사용)
     */
    public List<ProjectDTO> getOngoingProjectsByClientId(String clientMemberId) {
        List<ContractEntity> contractEntities =
                contractRepository.findByClientMemberIdAndStatus(clientMemberId, ContractStatus.SIGNED);

        // ContractEntity 에서 ProjectEntity 를 꺼내 ProjectDTO로 매핑
        return contractEntities.stream()
                .map(contract -> projectMapper.toDTO(contract.getProject()))
                .collect(Collectors.toList());
    }




}
