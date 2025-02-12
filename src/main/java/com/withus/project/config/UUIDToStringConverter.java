package com.withus.project.config;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.UUID;

@Converter(autoApply = true) // 모든 UUID-String 필드에 자동 적용
public class UUIDToStringConverter implements AttributeConverter<UUID, String> {

    // UUID → String (DB 저장 시 변환)
    @Override
    public String convertToDatabaseColumn(UUID uuid) {
        return uuid != null ? uuid.toString() : null;
    }

    // String → UUID (DB에서 가져올 때 변환)
    @Override
    public UUID convertToEntityAttribute(String dbData) {
        return dbData != null ? UUID.fromString(dbData) : null;
    }
}
