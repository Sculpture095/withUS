package com.withus.project.mapper.members;

import com.withus.project.domain.dto.members.parteners.CertificateDTO;
import com.withus.project.domain.members.CertificateEntity;
import com.withus.project.domain.members.PartnerEntity;
import org.mapstruct.*;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface CertificateMapper {

    @Mappings({
            @Mapping(target = "certificateIdx", ignore = true),
            @Mapping(target = "certificateId", expression = "java(entity.getCertificateId()!=null ? entity.getCertificateId().toString() : null)"), // UUID 매핑
            @Mapping(target = "partnerIdx", source = "partner.partnerIdx"), // ✅ 올바른 partner 매핑
            @Mapping(target = "certificateName", source = "certificateName"),
            @Mapping(target = "certificateDate", source = "certificateDate"),
            @Mapping(target = "institutionalName", source = "institutionalName")
    })
    CertificateDTO toDTO(CertificateEntity entity);

    @Mappings({
            @Mapping(target = "certificateIdx", ignore = true),
            @Mapping(target = "certificateId", qualifiedByName = "mapCertificateId"), // UUID 매핑
            @Mapping(target = "partner", source = "partnerIdx", qualifiedByName = "mapPartner"), // ✅ 변환 메서드 사용
            @Mapping(target = "certificateName", source = "certificateName"),
            @Mapping(target = "certificateDate", source = "certificateDate"),
            @Mapping(target = "institutionalName", source = "institutionalName")
    })
    CertificateEntity toEntity(CertificateDTO dto);

    @Named("mapPartner")
    default PartnerEntity mapPartner(Integer partnerIdx) { // ✅ UUID 기반 변환
        if (partnerIdx == null) return null;
        PartnerEntity partner = new PartnerEntity();
        partner.setPartnerIdx(partnerIdx);
        return partner;
    }

    @Named("mapCertificateId")
    default UUID mapCertificateId(String certificateId) {
        if (certificateId != null && certificateId.matches("^[0-9a-fA-F-]{36}$")) {
            return UUID.fromString(certificateId);
        }
        return null;
    }
}
