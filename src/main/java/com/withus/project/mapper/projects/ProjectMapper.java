package com.withus.project.mapper.projects;

import com.withus.project.domain.dto.projects.ProjectDTO;
import com.withus.project.domain.members.SelectSkillEntity;
import com.withus.project.domain.projects.ProjectEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {CaseMapper.class})
public interface ProjectMapper {

    @Mappings({
            @Mapping(target = "projectId", expression = "java(entity.getProjectId() != null ? entity.getProjectId().toString() : null)"),
            @Mapping(target = "clientIdx", source = "client.clientIdx"),
            @Mapping(target = "memberId", source = "client.member.id"),
            @Mapping(target = "projectName", source = "projectName"),
            @Mapping(target = "proStatement", expression = "java(entity.getProStatement().name())"), // ✅ ENUM 코드값
            @Mapping(target = "proStatementDescription", expression = "java(entity.getProStatement() != null ? entity.getProStatement().getDescription() : \"N/A\")"),
            @Mapping(target = "construction", expression = "java(entity.getConstruction().name())"), // ✅ ENUM 코드값
            @Mapping(target = "constructionDescription", expression = "java(entity.getConstruction() != null ? entity.getConstruction().getDescription() : \"N/A\")"), // ✅ ENUM 설명 저장
            @Mapping(target = "amount", source = "amount"),
            @Mapping(target = "duration", source = "duration"),
            @Mapping(target = "startDate", source = "startDate", dateFormat = "yyyy-MM-dd"),
            @Mapping(target = "closingDate", source = "closingDate", dateFormat = "yyyy-MM-dd"),
            @Mapping(target = "registrationDate", source = "registrationDate", dateFormat = "yyyy-MM-dd"),
            @Mapping(target = "requirement", source = "requirement"),
            @Mapping(target = "projectLocation", source = "projectLocation"),
            @Mapping(target = "teamSize", source = "teamSize"),
            @Mapping(target = "projectInfo", source = "projectInfo"),
            @Mapping(target = "isCompleted", source = "isCompleted"),
            @Mapping(target = "skills", expression = "java(mapSkills(entity.getSelectedSkills()))") // ✅ SelectSkillEntity 변환 추가
    })
    ProjectDTO toDTO(ProjectEntity entity);

    @Mappings({
            @Mapping(target = "projectId", qualifiedByName = "mapProjectId"), // ✅ 별도 메서드에서 UUID 변환 처리
            @Mapping(target = "client", source = "clientIdx", qualifiedByName = "mapClient"),
            @Mapping(target = "projectName", source = "projectName"),
            @Mapping(target = "proStatement", expression = "java(com.withus.project.domain.projects.ProjectStatus.valueOf(dto.getProStatement()))"),
            @Mapping(target = "amount", source = "amount"),
            @Mapping(target = "duration", source = "duration"),
            @Mapping(target = "startDate", source = "startDate", dateFormat = "yyyy-MM-dd"),
            @Mapping(target = "closingDate", source = "closingDate", dateFormat = "yyyy-MM-dd"),
            @Mapping(target = "registrationDate", source = "registrationDate", dateFormat = "yyyy-MM-dd"),
            @Mapping(target = "construction", expression = "java(dto.getConstruction() != null ? com.withus.project.domain.projects.Construction.valueOf(dto.getConstruction()) : null)"),
            @Mapping(target = "requirement", source = "requirement"),
            @Mapping(target = "projectLocation", source = "projectLocation"),
            @Mapping(target = "teamSize", source = "teamSize"),
            @Mapping(target = "projectInfo", source = "projectInfo"),
            @Mapping(target = "isCompleted", source = "isCompleted"),
            @Mapping(target = "selectedSkills", expression = "java(mapSkillsFromDTO(dto.getSkills()))") // ✅ DTO에서 Entity로 변환 추가
    })
    ProjectEntity toEntity(ProjectDTO dto);

    // ✅ UUID 변환을 위한 별도 메서드 추가
    @Named("mapProjectId")
    default UUID mapProjectId(String projectId) {
        if (projectId != null && projectId.matches("^[0-9a-fA-F-]{36}$")) {
            return UUID.fromString(projectId);
        }
        return null;
    }
    // ✅ SelectSkillEntity → List<String> 변환
    default List<String> mapSkills(List<SelectSkillEntity> skills) {
        if (skills == null) {
            return null;
        }
        return skills.stream()
                .map(skill -> skill.getSkillType().name())
                .collect(Collectors.toList());
    }

    // ✅ List<String> → List<SelectSkillEntity> 변환 (역변환)
    default List<SelectSkillEntity> mapSkillsFromDTO(List<String> skillNames) {
        if (skillNames == null) {
            return null;
        }
        return skillNames.stream()
                .map(skillName -> {
                    SelectSkillEntity skillEntity = new SelectSkillEntity();
                    skillEntity.setSkillType(com.withus.project.domain.members.SkillType.valueOf(skillName));
                    return skillEntity;
                })
                .collect(Collectors.toList());
    }
}
