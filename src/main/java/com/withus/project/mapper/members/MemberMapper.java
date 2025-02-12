package com.withus.project.mapper.members;

import com.withus.project.domain.dto.members.MemberDTO;
import com.withus.project.domain.members.MemberEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface MemberMapper {
    @Mappings({
            @Mapping(target = "pcaType", expression = "java(com.withus.project.domain.members.PcaType.valueOf(dto.getPcaType()))"),
            @Mapping(target = "userType", expression = "java(com.withus.project.domain.members.UserType.valueOf(dto.getUserType()))"),
            @Mapping(target = "rankType", expression = "java(dto.getRankType() == null || dto.getRankType().isEmpty() ? com.withus.project.domain.members.RankType.BASIC : com.withus.project.domain.members.RankType.valueOf(dto.getRankType()))"),
            @Mapping(target = "id", source = "id"),
            @Mapping(target = "password", source = "password"),
            @Mapping(target = "nickname", source = "nickname"),
            @Mapping(target = "name", source = "name"),
            @Mapping(target = "email", source = "email"),
            @Mapping(target = "phone", source = "phone"),
            @Mapping(target = "client", ignore = true),
            @Mapping(target = "partner", ignore = true),
            @Mapping(target = "myPage", ignore = true)
    })
    MemberEntity toEntity(MemberDTO dto);

    @Mappings({
            @Mapping(target = "pcaType", expression = "java(entity.getPcaType().name())"),
            @Mapping(target = "userType", expression = "java(entity.getUserType().name())"),
            @Mapping(target = "rankType", expression = "java(entity.getRankType().name())"),
            @Mapping(target = "memberIdx", source = "memberIdx"),
            @Mapping(target = "id", source = "id"),
            @Mapping(target = "nickname", source = "nickname"),
            @Mapping(target = "name", source = "name"),
            @Mapping(target = "email", source = "email"),
            @Mapping(target = "phone", source = "phone")
    })
    MemberDTO toDTO(MemberEntity entity);
}
