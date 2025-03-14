package com.withus.project.mapper.members;

import com.withus.project.dto.members.SelectSkillDTO;
import com.withus.project.domain.members.SelectSkillEntity;
import com.withus.project.domain.members.SkillType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring", imports = SkillType.class)
public interface SelectSkillMapper {

    @Mappings({
            @Mapping(target = "selectSkillIdx", ignore = true),
            @Mapping(target = "selectSkillId", source = "selectSkillId"), // UUID 매핑
            @Mapping(target = "skillCode", expression = "java(entity.getSkillType().name())"), // ✅ Enum 변환
            @Mapping(target = "projectId", source = "project.projectId"), // ✅ ProjectEntity에서 projectId 가져오기
            @Mapping(target = "careerDuration", source = "careerDuration") // 경력 기간

    })
    SelectSkillDTO toDTO(SelectSkillEntity entity);

    @Mappings({
            @Mapping(target = "selectSkillIdx", ignore = true),
            @Mapping(target = "selectSkillId", source = "selectSkillId"), // UUID 매핑
            @Mapping(target = "skillType", expression = "java(SkillType.valueOf(dto.getSkillCode()))"), // ✅ Enum 변환
            @Mapping(target = "project", ignore = true), // ❌ ProjectEntity는 서비스 계층에서 처리하도록 변경
            @Mapping(target = "careerDuration", source = "careerDuration") // 경력 기간

    })
    SelectSkillEntity toEntity(SelectSkillDTO dto);


}
