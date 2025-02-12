package com.withus.project.mapper.members;

import com.withus.project.domain.dto.members.QuestionDTO;
import com.withus.project.domain.members.MemberEntity;
import com.withus.project.domain.members.QuestionEntity;
import org.mapstruct.*;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface QuestionMapper {

    @Mappings({
            @Mapping(target = "questionIdx", ignore = true),
            @Mapping(target = "questionId", qualifiedByName = "mapQuestionId"), // UUID 매핑
            @Mapping(target = "member.memberIdx", source = "memberIdx"),
            @Mapping(target = "subject", source = "subject"),
            @Mapping(target = "content", source = "content"),
            @Mapping(target = "attachment", source = "attachment")
    })
    QuestionEntity toEntity(QuestionDTO dto);

    @Mappings({
            @Mapping(target = "questionIdx", ignore = true),
            @Mapping(target = "questionId", expression = "java(entity.getQuestionId() != null ? entity.getQuestionId().toString() : null)"), // UUID 매핑
            @Mapping(target = "memberIdx", source = "member.memberIdx"),
            @Mapping(target = "subject", source = "subject"),
            @Mapping(target = "content", source = "content"),
            @Mapping(target = "attachment", source = "attachment")
    })
    QuestionDTO toDTO(QuestionEntity entity);

    @Named("mapQuestionId")
    default UUID mapQuestionId(String questionId) { // ✅ UUID 기반 변환
        if (questionId != null && questionId.matches("[0-9a-fA-F]{32}")) {
            return UUID.fromString(questionId);
        }
        return null;
    }
}

