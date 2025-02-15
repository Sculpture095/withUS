package com.withus.project.mapper.members;

import com.withus.project.domain.dto.members.MyPageDTO;
import com.withus.project.domain.members.MyPageEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface MyPageMapper {

    // DTO -> Entity
    @Mappings({
            @Mapping(target = "myPageIdx", ignore = true),
            @Mapping(target = "myPageId", qualifiedByName = "mapMyPageId"),
            @Mapping(target = "member.memberIdx", source = "memberIdx"),
            @Mapping(target = "address", source = "address"),
            @Mapping(target = "profile", source = "profile"),
            @Mapping(target = "bankName", source = "bankName"),
            @Mapping(target = "account", source = "account"),
            @Mapping(target = "introduce", source = "introduce"),
            @Mapping(target = "zipcode", source = "zipcode"),
            @Mapping(target = "businessNum", ignore = true)
    })
    MyPageEntity toEntity(MyPageDTO dto);

    // Entity -> DTO
    @Mappings({
            @Mapping(target = "myPageIdx", ignore = true),
            @Mapping(target = "myPageId", expression = "java(entity.getMyPageId() != null ? entity.getMyPageId().toString() : null)"),
            @Mapping(target = "memberIdx", source = "member.memberIdx"),
            @Mapping(target = "address", source = "address"),
            @Mapping(target = "profile", source = "profile"),
            @Mapping(target = "bankName", source = "bankName"),
            @Mapping(target = "account", source = "account"),
            @Mapping(target = "introduce", source = "introduce"),
            @Mapping(target = "zipcode", source = "zipcode"),
            @Mapping(target = "businessNum", source = "businessNum")
    })
    MyPageDTO toDTO(MyPageEntity entity);

    @Named("mapMyPageId")
    default String mapMyPageId(String myPageId) {
        if (myPageId != null && myPageId.matches("^[0-9a-fA-F-]{36}$")) {
            return myPageId;
        }
        return null;
    }
}
