package com.withus.project.mapper.projects;

import com.withus.project.dto.projects.PaymentDTO;
import com.withus.project.domain.projects.PaymentEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    // 1) DTO -> Entity
    @Mappings({
            @Mapping(target = "paymentIdx", ignore = true),         // DB 자동생성(ID)
            // ContractEntity의 어떤 필드를 set할지에 따라 달라집니다.
            // 예: contractId(문자열)라는 필드가 ContractEntity 내부에 있어야 합니다.
            @Mapping(target = "contract.contractId", source = "contractId"),

            @Mapping(target = "impUid", source = "impUid"),
            @Mapping(target = "merchantUid", source = "merchantUid"),
            @Mapping(target = "payMethod", source = "payMethod"),
            @Mapping(target = "status", source = "status"),
            @Mapping(target = "amount", source = "amount"),
            @Mapping(target = "paidAt", source = "paidAt"),
            @Mapping(target = "cancelledAt", source = "cancelledAt"),
            @Mapping(target = "cancelAmount", source = "cancelAmount"),
            @Mapping(target = "failReason", source = "failReason")

    })
    PaymentEntity toEntity(PaymentDTO dto);

    // 2) Entity -> DTO
    @Mappings({
            // paymentIdx는 그대로 매핑
            @Mapping(target = "paymentIdx", source = "paymentIdx"),

            // ContractEntity에서 contractId 필드를 뽑아 DTO의 contractId로 매핑
            @Mapping(target = "contractId", source = "contract.contractId"),

            @Mapping(target = "impUid", source = "impUid"),
            @Mapping(target = "merchantUid", source = "merchantUid"),
            @Mapping(target = "payMethod", source = "payMethod"),
            @Mapping(target = "status", source = "status"),
            @Mapping(target = "amount", source = "amount"),
            @Mapping(target = "paidAt", source = "paidAt"),
            @Mapping(target = "cancelledAt", source = "cancelledAt"),
            @Mapping(target = "cancelAmount", source = "cancelAmount"),
            @Mapping(target = "failReason", source = "failReason")

    })
    PaymentDTO toDTO(PaymentEntity entity);
}
