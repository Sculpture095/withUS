package com.withus.project.mapper.projects;

import com.withus.project.domain.dto.projects.PaymentDTO;
import com.withus.project.domain.projects.PaymentEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    @Mappings({
            @Mapping(target = "paymentIdx", ignore = true),
            @Mapping(target = "paymentId", qualifiedByName = "mapPaymentId"), // UUID 매핑
            @Mapping(target = "contract.contractId", source = "contractId"),
            @Mapping(target = "amount", source = "amount"),
            @Mapping(target = "fee", source = "fee"),
            @Mapping(target = "method", source = "method"),
            @Mapping(target = "account", source = "account")
    })
    PaymentEntity toEntity(PaymentDTO dto);

    @Mappings({
            @Mapping(target = "paymentIdx", ignore = true),
            @Mapping(target = "paymentId", expression = "java(entity.getPaymentId() != null ? entity.getPaymentId().toString() : null)"), // UUID 매핑
            @Mapping(target = "contractId", source = "contract.contractId"),
            @Mapping(target = "amount", source = "amount"),
            @Mapping(target = "fee", source = "fee"),
            @Mapping(target = "method", source = "method"),
            @Mapping(target = "account", source = "account")
    })
    PaymentDTO toDTO(PaymentEntity entity);

    @Named("mapPaymentId")
    default UUID mapPaymentId(String paymentId) {
        if(paymentId != null &&paymentId.matches("[0-9a-fA-F]{32}")) {
            return UUID.fromString(paymentId);
        }
            return null;


    }
}
