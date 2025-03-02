package com.withus.project.mapper.projects;

import com.withus.project.dto.projects.CaseDTO;
import com.withus.project.dto.projects.ContractDTO;
import com.withus.project.domain.members.PartnerEntity;
import com.withus.project.domain.projects.CaseEntity;
import com.withus.project.domain.projects.ContractEntity;
import com.withus.project.domain.projects.ProjectEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ContractMapper {

    @Mappings({
            @Mapping(target = "contractIdx", ignore = true), //
            @Mapping(target = "contractId",expression = "java(entity.getContractId() != null ? entity.getContractId().toString() : null)"), // UUID 매핑
            @Mapping(target = "partnerIdx", source = "partner.partnerIdx"), // ✅ partnerId → partner.partnerIdx
            @Mapping(target = "projectId", source = "project.projectId"), // ✅ projectId → project.projectId
            @Mapping(target = "status", source = "status"),
            @Mapping(target = "amount", source = "amount"),
            @Mapping(target = "detail", source = "detail"),
            @Mapping(target = "contractName", source = "contractName"),
            @Mapping(target = "contractDate", source = "contractDate"),
            @Mapping(target = "apiCode", source = "apiCode"),  // <- apiCode 매핑 추가
            @Mapping(target = "caseEntities", source = "caseEntities", qualifiedByName = "mapCases") // ✅ 고객 사례 리스트 매핑 추가
    })
    ContractDTO toDTO(ContractEntity entity);

    @Mappings({
            @Mapping(target = "contractIdx", ignore = true),
            @Mapping(target = "contractId", qualifiedByName = "mapContractId"), // UUID 매핑
            @Mapping(target = "partner", source = "partnerIdx", qualifiedByName = "mapPartner"), // ✅ 변환 메서드 사용
            @Mapping(target = "project", source = "projectId", qualifiedByName = "mapProject"), // ✅ 변환 메서드 사용
            @Mapping(target = "status", source = "status"),
            @Mapping(target = "amount", source = "amount"),
            @Mapping(target = "detail", source = "detail"),
            @Mapping(target = "contractName", source = "contractName"),
            @Mapping(target = "contractDate", source = "contractDate"),
            @Mapping(target = "apiCode", source = "apiCode"),  // <- apiCode 매핑 추가
            @Mapping(target = "caseEntities", ignore = true) // ✅ Entity 생성 시에는 고객 사례를 매핑하지 않음
    })
    ContractEntity toEntity(ContractDTO dto);

    @Named("mapPartner")
    default PartnerEntity mapPartner(Integer partnerIdx) { // ✅ UUID 기반 변환
        if (partnerIdx == null) return null;
        PartnerEntity partner = new PartnerEntity();
        partner.setPartnerIdx(partnerIdx);
        return partner;
    }

    @Named("mapProject")
    default ProjectEntity mapProject(String projectId) { // ✅ UUID 기반 변환
        if (projectId == null) return null;
        ProjectEntity project = new ProjectEntity();
        project.setProjectId(UUID.fromString(projectId));
        return project;
    }
    @Named("mapCases")
    default List<CaseDTO> mapCases(List<CaseEntity> caseEntities) { // ✅ 고객 사례 매핑 추가
        if (caseEntities == null) return null;
        return caseEntities.stream()
                .map(caseEntity -> CaseDTO.builder()
                        .caseIdx(caseEntity.getCaseIdx())
                        .caseId(caseEntity.getCaseId().toString())
                        .contractId(caseEntity.getContract().getContractId().toString()) // ✅ contractId 설정
                        .title(caseEntity.getTitle())
                        .content(caseEntity.getContent())
                        .createDate(caseEntity.getCreateDate().toString())
                        .rating(caseEntity.getRating()) // Enum → String 변환
                        .caseImg(caseEntity.getCaseImg())
                        .build())
                .collect(Collectors.toList());
    }

    @Named("mapContractId")
    default UUID mapContractId(String contractId) { // ✅ UUID 기반 변환
        if (contractId != null && contractId.matches("[0-9a-fA-F]{32}")) {
            return UUID.fromString(contractId);
        }
        return null;
    }
}


