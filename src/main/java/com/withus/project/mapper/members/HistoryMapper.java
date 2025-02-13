package com.withus.project.mapper.members;


import com.withus.project.domain.dto.members.parteners.HistoryDTO;
import com.withus.project.domain.members.HistoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface HistoryMapper {

    @Mappings({
            @Mapping(target = "historyIdx", ignore = true),
            @Mapping(target = "historyId", qualifiedByName = "mapHistoryId"), // UUID 매핑
            @Mapping(target = "partner.partnerIdx", source = "partnerIdx"),
            @Mapping(target = "companyName", source = "companyName"),
            @Mapping(target = "joinDate", source = "joinDate"),
            @Mapping(target = "exitDate", source = "exitDate"),
            @Mapping(target = "duration", source = "duration"),
            @Mapping(target = "work", source = "work")
    })
    HistoryEntity toEntity(HistoryDTO dto);

    @Mappings({
            @Mapping(target = "historyIdx", ignore = true),
            @Mapping(target = "historyId",expression = "java(entity.getHistoryId() != null ? entity.getHistoryId().toString() : null)" ), // UUID 매핑
            @Mapping(target = "partnerIdx", source = "partner.partnerIdx"),
            @Mapping(target = "companyName", source = "companyName"),
            @Mapping(target = "joinDate", source = "joinDate"),
            @Mapping(target = "exitDate", source = "exitDate"),
            @Mapping(target = "duration", source = "duration"),
            @Mapping(target = "work", source = "work")
    })
    HistoryDTO toDTO(HistoryEntity entity);

    @Named("mapHistoryId")
    default UUID mapHistoryId(String historyId) {
        if (historyId != null && historyId.matches("^[0-9a-fA-F-]{36}$")) {
            return UUID.fromString(historyId);
        }
        return null;
    }
}


