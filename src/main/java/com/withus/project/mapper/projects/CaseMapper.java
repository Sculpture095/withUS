package com.withus.project.mapper.projects;

import com.withus.project.domain.dto.members.SelectSkillDTO;
import com.withus.project.domain.dto.projects.CaseDTO;
import com.withus.project.domain.members.*;
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
public interface CaseMapper {

    @Mappings({
            @Mapping(target = "caseIdx", ignore = true),
            @Mapping(target = "caseId",expression = "java(entity.getCaseId() != null ? entity.getCaseId().toString() : null)"), // UUID 매핑
            @Mapping(target = "contractId", source = "contract.contractId"), // ✅ ContractEntity에서 contractId 추출
            @Mapping(target = "createDate", ignore = true), // ✅ 자동 생성되므로 무시
            @Mapping(target = "relatedTechs", source = "relatedTechs", qualifiedByName = "mapRelatedTechDTOs"), // ✅ 변환 메서드 추가
            @Mapping(target = "title", source = "title"),
            @Mapping(target = "content", source = "content"),
            @Mapping(target = "caseImg", source = "caseImg"),
            @Mapping(target = "thumbnail", source = "thumbnail"),
            @Mapping(target = "caseAmount", source = "caseAmount"),
            @Mapping(target = "timeline", source = "timeline"),
            @Mapping(target = "rating", source = "rating")
    })
    CaseDTO toDTO(CaseEntity entity);

    @Mappings({
            @Mapping(target = "caseIdx", ignore = true),
            @Mapping(target = "caseId", qualifiedByName = "mapCaseId"), // UUID 매핑
            @Mapping(target = "contract", source = "contractId", qualifiedByName = "mapContract"), // ✅ UUID 변환 메서드 사용
            @Mapping(target = "createDate", ignore = true), // ✅ 자동 생성되므로 무시
            @Mapping(target = "relatedTechs", source = "relatedTechs", qualifiedByName = "mapRelatedTechEntities"), // ✅ 변환 메서드 추가
            @Mapping(target = "title", source = "title"),
            @Mapping(target = "content", source = "content"),
            @Mapping(target = "caseImg", source = "caseImg"),
            @Mapping(target = "thumbnail", source = "thumbnail"),
            @Mapping(target = "caseAmount", source = "caseAmount"),
            @Mapping(target = "timeline", source = "timeline"),
            @Mapping(target = "rating", source = "rating")
    })
    CaseEntity toEntity(CaseDTO dto);

    @Named("mapContract")
    default ContractEntity mapContract(UUID contractId) { // ✅ UUID 기반 변환
        if (contractId == null) return null;
        ContractEntity contract = new ContractEntity();
        contract.setContractId(contractId);
        return contract;
    }

    @Named("mapClient")
    default ClientEntity mapClient(Integer clientIdx) { // ✅ UUID 기반 변환
        if (clientIdx == null) return null;
        ClientEntity client = new ClientEntity();
        client.setClientIdx(clientIdx);
        return client;
    }

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

    @Named("mapRelatedTechDTOs")
    default List<SelectSkillDTO> mapRelatedTechDTOs(List<SelectSkillEntity> relatedTechs) {
        if (relatedTechs == null) return null;
        return relatedTechs.stream()
                .map(skillEntity -> new SelectSkillDTO(
                        skillEntity.getSelectSkillIdx(), // ✅ selectSkillIdx 추가
                        skillEntity.getSelectSkillId().toString(), // 🔄 UUID → String 변환
                        skillEntity.getSkillType().name(), // 🔄 Enum → String 변환
                        skillEntity.getProject() != null ? skillEntity.getProject().getProjectId().toString() : null // 🔄 프로젝트 ID (nullable)
                ))
                .collect(Collectors.toList());
    }

    @Named("mapRelatedTechEntities")
    default List<SelectSkillEntity> mapRelatedTechEntities(List<SelectSkillDTO> relatedTechs) {
        if (relatedTechs == null) return null;
        return relatedTechs.stream()
                .map(skillDTO -> {
                    SelectSkillEntity skillEntity = new SelectSkillEntity();
                    skillEntity.setSelectSkillIdx(skillDTO.getSelectSkillIdx()); // ✅ selectSkillIdx 추가
                    skillEntity.setSelectSkillId(skillDTO.getSelectSkillId().equals("") ? null : UUID.fromString(skillDTO.getSelectSkillId())); // 🔄 String → UUID 변환


                    // SkillType Enum 변환
                    skillEntity.setSkillType(SkillType.valueOf(skillDTO.getSkillCode()));


                    return skillEntity;
                })
                .collect(Collectors.toList());
    }

    @Named("mapCaseId")
    default UUID mapCaseId(String caseId) { // ✅ UUID 기반 변환
        if (caseId != null && caseId.matches("[0-9a-fA-F]{32}")) {
            return UUID.fromString(caseId);
        }
        return null;
    }
}


