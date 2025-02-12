package com.withus.project.mapper.boards;

import com.withus.project.domain.boards.BoardEntity;
import com.withus.project.domain.dto.boards.BoardDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;


@Mapper(componentModel = "spring")
public interface BoardMapper {

    @Mappings({
            @Mapping(target = "boardIdx", ignore = true),
            @Mapping(target = "boardId", source = "boardId"), // UUID 매핑
            @Mapping(target = "boardType", expression = "java(com.withus.project.domain.boards.BoardType.valueOf(dto.getBoardType()))"),
            @Mapping(target = "member.memberIdx", source = "memberIdx"),
            @Mapping(target = "createDate", expression = "java(java.time.LocalDate.now())"),
            @Mapping(target = "viewCount", constant = "0"),
            @Mapping(target = "likeCount", constant = "0")
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
            @Mapping(target = "createDate", expression = "java(entity.getCreateDate().toString())"),
            @Mapping(target = "viewCount", source = "viewCount"),
            @Mapping(target = "likeCount", source = "likeCount")
    })
    BoardDTO toDTO(BoardEntity entity);
}
