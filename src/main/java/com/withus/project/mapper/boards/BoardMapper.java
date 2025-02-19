package com.withus.project.mapper.boards;

import com.withus.project.domain.boards.BoardEntity;
import com.withus.project.domain.dto.boards.BoardDTO;
import com.withus.project.mapper.DateTimeMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@Mapper(componentModel = "spring" , uses = { RemarkMapper.class, DateTimeMapper.class})
public interface BoardMapper {


    @Mappings({
            @Mapping(target = "boardIdx", ignore = true),
            @Mapping(target = "boardId", source = "boardId"), // UUID 매핑
            @Mapping(target = "boardType", expression = "java(com.withus.project.domain.boards.BoardType.valueOf(dto.getBoardType()))"),
            @Mapping(target = "member.memberIdx", source = "memberIdx"),
            @Mapping(target = "createDate", source = "createDate", qualifiedByName = "stringToLocalDateTime"), // ✅ DateTimeMapper의 메서드 사용
            @Mapping(target = "viewCount", constant = "0"),
            @Mapping(target = "likeCount", constant = "0"),
            @Mapping(target = "remarks", ignore = true) // ✅ Entity 변환 시 댓글은 무시
    })
    BoardEntity toEntity(BoardDTO dto);

    @Mappings({
            @Mapping(target = "boardIdx", ignore = true),
            @Mapping(target = "boardId", source = "boardId"), // UUID 매핑
            @Mapping(target = "memberIdx", source = "member.memberIdx"),
            @Mapping(target = "boardType", expression = "java(entity.getBoardType().name())"),
            @Mapping(target = "subject", source = "subject"),
            @Mapping(target = "content", source = "content"),
            @Mapping(target = "filePath", source = "filePath"),
            @Mapping(target = "createDate", source = "createDate", qualifiedByName = "localDateTimeToString"), // ✅ DateTimeMapper의 메서드 사용
            @Mapping(target = "viewCount", source = "viewCount"),
            @Mapping(target = "likeCount", source = "likeCount"),
            @Mapping(target = "remarks", source = "remarks") // ✅ 댓글 목록 매핑
    })
    BoardDTO toDTO(BoardEntity entity);


}
