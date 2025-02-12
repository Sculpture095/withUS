package com.withus.project.mapper.members;

import com.withus.project.domain.dto.members.parteners.PortfolioDTO;
import com.withus.project.domain.members.PartnerEntity;
import com.withus.project.domain.members.PortfolioEntity;
import com.withus.project.domain.projects.CaseEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface PortfolioMapper {

    @Mappings({
            @Mapping(target = "portfolioIdx", ignore = true), // idx는 노출 금지
            @Mapping(target = "portfolioId", qualifiedByName = "mapPortfolioId"), // UUID 매핑
            @Mapping(target = "partner", source = "partnerIdx", qualifiedByName = "mapPartner"), // ✅ 변환 메서드 추가
            @Mapping(target = "portfolioTitle", source = "portfolioTitle"),
            @Mapping(target = "portfolioContext", source = "portfolioContext"),
            @Mapping(target = "startDate", source = "startDate"),
            @Mapping(target = "endDate", source = "endDate"),
            @Mapping(target = "portfolioImg", source = "portfolioImg"),
            @Mapping(target = "thumbnail", source = "thumbnail"),
            @Mapping(target = "resultUrl", source = "resultUrl"),
            @Mapping(target = "publicOk", source = "publicOk")
    })
    PortfolioEntity toEntity(PortfolioDTO dto);

    @Mappings({
            @Mapping(target = "portfolioIdx", ignore = true), // idx는 노출 금지
            @Mapping(target = "portfolioId",  expression = "java(entity.getPortfolioId() != null ? entity.getPortfolioId().toString() : null)"), // UUID 매핑
            @Mapping(target = "partnerIdx", source = "partner.partnerIdx"), // ✅ partnerIdx로 변경
            @Mapping(target = "portfolioTitle", source = "portfolioTitle"),
            @Mapping(target = "portfolioContext", source = "portfolioContext"),
            @Mapping(target = "startDate", source = "startDate"),
            @Mapping(target = "endDate", source = "endDate"),
            @Mapping(target = "portfolioImg", source = "portfolioImg"),
            @Mapping(target = "thumbnail", source = "thumbnail"),
            @Mapping(target = "resultUrl", source = "resultUrl"),
            @Mapping(target = "publicOk", source = "publicOk")
    })
    PortfolioDTO toDTO(PortfolioEntity entity);

    @Named("mapPartner")
    default PartnerEntity mapPartner(Integer partnerIdx) { // ✅ UUID 기반 변환
        if (partnerIdx == null) return null;
        PartnerEntity partner = new PartnerEntity();
        partner.setPartnerIdx(partnerIdx);
        return partner;
    }

    @Named("mapPortfolioId")
    default UUID mapPortfolioId(String portfolioId) { // ✅ UUID 기반 변환
        if (portfolioId != null&& portfolioId.matches("[0-9a-fA-F]{32}")) {
            return UUID.fromString(portfolioId);
        }
        return null;
    }


}
