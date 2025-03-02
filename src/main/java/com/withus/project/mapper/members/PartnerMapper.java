package com.withus.project.mapper.members;

import com.withus.project.dto.members.parteners.PartnerDTO;
import com.withus.project.domain.members.PartnerEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring", uses = {PortfolioMapper.class})
public interface PartnerMapper {
    @Mappings({
            @Mapping(target = "partnerIdx", ignore = true),
            @Mapping(target = "member.memberIdx", source = "memberIdx"), // 회원 ID 매핑
            @Mapping(target = "degree", source = "degree"), // 최종학력 매핑
            @Mapping(target = "schoolName", source = "schoolName"), // 학교명 매핑
            @Mapping(target = "major", source = "major"), // 전공 매핑
            @Mapping(target = "admission", source = "admission"), // 입학년도 매핑
            @Mapping(target = "graduation", source = "graduation"), // 졸업년도 매핑
            @Mapping(target = "location", source = "location"), // 거주 지역 매핑
            @Mapping(target = "portfolios", ignore = true) // 포트폴리오 매핑 추가 필요
    })
    PartnerEntity toEntity(PartnerDTO dto);

    @Mappings({
            @Mapping(target = "partnerIdx", ignore = true),
            @Mapping(target = "memberIdx", source = "member.memberIdx"), // 회원 ID 매핑
            @Mapping(target = "degree", source = "degree"), // 최종학력 매핑
            @Mapping(target = "schoolName", source = "schoolName"), // 학교명 매핑
            @Mapping(target = "major", source = "major"), // 전공 매핑
            @Mapping(target = "admission", source = "admission"), // 입학년도 매핑
            @Mapping(target = "graduation", source = "graduation"), // 졸업년도 매핑
            @Mapping(target = "location", source = "location"), // 거주 지역 매핑
            @Mapping(target = "portfolios", source = "portfolios") // 포트폴리오 매핑 추가
    })
    PartnerDTO toDTO(PartnerEntity entity);
}
