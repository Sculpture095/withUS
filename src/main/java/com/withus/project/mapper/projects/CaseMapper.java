package com.withus.project.mapper.projects;

import com.withus.project.dto.members.SelectSkillDTO;
import com.withus.project.dto.projects.CaseDTO;
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

    // E → D
    // E → D
    @Mappings({
            @Mapping(target = "caseIdx", source = "caseIdx"),
            @Mapping(target = "caseId", expression = "java(entity.getCaseId()!=null?entity.getCaseId().toString():null)"),

            // ★ contractId = entity.contract.contractId (UUID→String)
            @Mapping(target = "contractId",
                    expression = "java(entity.getContract()!=null && entity.getContract().getContractId()!=null? entity.getContract().getContractId().toString(): null)"),

            // createDate = LocalDate -> String
            @Mapping(target = "createDate",
                    expression = "java(entity.getCreateDate()!=null? entity.getCreateDate().toString(): null)"),

            @Mapping(target = "relatedTechs", source = "relatedTechs", qualifiedByName = "mapRelatedTechDTOs"),
            @Mapping(target = "title", source = "title"),
            @Mapping(target = "content", source = "content"),
            @Mapping(target = "shortContent", source = "shortContent"),
            @Mapping(target = "caseImg", source = "caseImg"),
            @Mapping(target = "rating", source = "rating")
    })
    CaseDTO toDTO(CaseEntity entity);

    @Mappings({
            @Mapping(target = "caseIdx", ignore = true),
            @Mapping(target = "caseId", qualifiedByName = "mapCaseId"),
            // ★ contract (String→UUID→ContractEntity)
            @Mapping(target = "contract", source = "contractId", qualifiedByName = "mapContract"),

            @Mapping(target = "createDate", ignore = true),
            @Mapping(target = "relatedTechs", source = "relatedTechs", qualifiedByName = "mapRelatedTechEntities"),
            @Mapping(target = "title", source = "title"),
            @Mapping(target = "content", source = "content"),
            @Mapping(target = "shortContent", source = "shortContent"),
            @Mapping(target = "caseImg", source = "caseImg"),
            @Mapping(target = "rating", source = "rating")
    })
    CaseEntity toEntity(CaseDTO dto);

    // String -> UUID -> ContractEntity
    @Named("mapContract")
    default ContractEntity mapContract(String contractIdStr) {
        if (contractIdStr == null) return null;
        UUID uuid = UUID.fromString(contractIdStr);
        ContractEntity contract = new ContractEntity();
        contract.setContractId(uuid); // DB의 contract_id 컬럼
        return contract;
    }

    @Named("mapCaseId")
    default UUID mapCaseId(String caseIdStr) {
        if (caseIdStr == null) return null;
        return UUID.fromString(caseIdStr);
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


}


