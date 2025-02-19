package com.withus.project.mapper;

import org.mapstruct.Named;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class DateTimeMapper {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Named("localDateTimeToString")
    public String localDateTimeToString(LocalDateTime date) {
        return date != null ? date.format(FORMATTER) : null;
    }

    @Named("stringToLocalDateTime")
    public LocalDateTime stringToLocalDateTime(String date) {
        return date != null ? LocalDateTime.parse(date, FORMATTER) : null;
    }
}
