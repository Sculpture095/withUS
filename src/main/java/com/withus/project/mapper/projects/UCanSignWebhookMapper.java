package com.withus.project.mapper.projects;

import com.withus.project.domain.projects.ContractEntity;
import com.withus.project.domain.projects.UCanSignWebhookEntity;
import com.withus.project.dto.projects.UCanSignWebhookDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface UCanSignWebhookMapper {

    /**
     * [Entity → DTO] 변환
     */
    @Mappings({
            // UUID → String: uCanSignWebhookId
            @Mapping(target = "ucanSignWebhookId",
                    expression = "java(entity.getUcanSignWebhookId() != null ? entity.getUcanSignWebhookId().toString() : null)"),
            // LocalDateTime → String: createdAt
            @Mapping(target = "createdAt",
                    expression = "java(entity.getCreatedAt() != null ? entity.getCreatedAt().toString() : null)"),
            // ContractEntity → String: contractId
            @Mapping(target = "contractId",
                    expression = "java(entity.getContract() != null && entity.getContract().getContractId() != null ? entity.getContract().getContractId().toString() : null)")
    })
    UCanSignWebhookDTO toDTO(UCanSignWebhookEntity entity);

    /**
     * [DTO → Entity] 변환
     */
    @Mappings({
            // DB 자동 증가 컬럼은 ignore 처리
            @Mapping(target = "ucanSignWebhookIdx", ignore = true),
            // String → UUID 변환
            @Mapping(target = "ucanSignWebhookId", qualifiedByName = "mapUcanSignWebhookId"),
            // createdAt은 DB에서 DEFAULT CURRENT_TIMESTAMP 처리
            @Mapping(target = "createdAt", ignore = true),
            // DTO의 contractId(String) → ContractEntity(UUID)
            @Mapping(target = "contract", source = "contractId", qualifiedByName = "mapContract")
    })
    UCanSignWebhookEntity toEntity(UCanSignWebhookDTO dto);

    /**
     * [String → UUID] 변환 메서드
     */
    @Named("mapUcanSignWebhookId")
    default UUID mapUcanSignWebhookId(String webhookId) {
        if (webhookId == null || webhookId.isEmpty()) {
            return null;
        }
        return UUID.fromString(webhookId);
    }

    /**
     * [String → ContractEntity] 변환 메서드
     */
    @Named("mapContract")
    default ContractEntity mapContract(String contractId) {
        if (contractId == null || contractId.isEmpty()) {
            return null;
        }
        ContractEntity contract = new ContractEntity();
        contract.setContractId(UUID.fromString(contractId));
        return contract;
    }
}
