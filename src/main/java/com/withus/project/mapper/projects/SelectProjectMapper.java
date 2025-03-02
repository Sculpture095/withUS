package com.withus.project.mapper.projects;

import com.withus.project.dto.projects.SelectProjectDTO;
import com.withus.project.domain.members.PartnerEntity;
import com.withus.project.domain.projects.ProjectEntity;
import com.withus.project.domain.projects.SelectProjectEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface SelectProjectMapper {

    @Mappings({
            @Mapping(target = "selectProjectIdx", ignore = true),
            @Mapping(target = "selectProjectId", expression = "java(entity.getSelectProjectId() != null ? entity.getSelectProjectId().toString() : null)"), // UUID 매핑
            @Mapping(target = "partnerIdx", source = "partner.partnerIdx"), // ✅ partner 객체에서 partnerIdx 추출
            @Mapping(target = "projectId", source = "project.projectId"), // ✅ project 객체에서 projectId 추출
            @Mapping(target = "yn", source = "yn")
    })
    SelectProjectDTO toDTO(SelectProjectEntity entity);

    @Mappings({
            @Mapping(target = "selectProjectIdx", ignore = true),
            @Mapping(target = "selectProjectId", qualifiedByName = "mapSelectProjectId"), // ✅ 변환 메서드 추가
            @Mapping(target = "partner", source = "partnerIdx", qualifiedByName = "mapPartner"), // ✅ 변환 메서드 추가
            @Mapping(target = "project", source = "projectId", qualifiedByName = "mapProject"), // ✅ 변환 메서드 추가
            @Mapping(target = "yn", source = "dto.yn")
    })
    SelectProjectEntity toEntity(SelectProjectDTO dto);

    @Named("mapPartner")
    default PartnerEntity mapPartner(Integer partnerIdx) {
        if (partnerIdx == null) return null;
        PartnerEntity partner = new PartnerEntity();
        partner.setPartnerIdx(partnerIdx);
        return partner;
    }

    @Named("mapProject")
    default ProjectEntity mapProject(String projectId) {
        if (projectId == null) return null;
        ProjectEntity project = new ProjectEntity();
        project.setProjectId(UUID.fromString(projectId));
        return project;
    }

    @Named("mapSelectProjectId")
    default UUID mapSelectProjectId(String selectProjectId) {
        if(selectProjectId != null && selectProjectId.matches("[0-9a-fA-F]{32}")) {
            return UUID.fromString(selectProjectId);
        }
        return null;
    }
}
