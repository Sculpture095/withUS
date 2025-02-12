package com.withus.project.mapper.boards;

import com.withus.project.domain.boards.BoardEntity;
import com.withus.project.domain.boards.RemarkEntity;
import com.withus.project.domain.dto.boards.RemarkDTO;
import com.withus.project.domain.members.MemberEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface RemarkMapper {

    @Mappings({
            @Mapping(target = "remarkIdx", ignore = true),
            @Mapping(target = "remarkId", source = "remarkId"), // UUID 매핑
            @Mapping(target = "boardId", source = "boardType.boardId"), // ✅ boardType을 사용해야 함
            @Mapping(target = "memberIdx", source = "member.memberIdx"), // ✅ memberIdx를 사용해야 함
            @Mapping(target = "remarks", source = "remarks"),
            @Mapping(target = "createDate", expression = "java(entity.getCreateDate().toString())"),
            @Mapping(target = "likeCount", source = "likeCount")
    })
    RemarkDTO toDTO(RemarkEntity entity);

    @Mappings({
            @Mapping(target = "remarkIdx", ignore = true),
            @Mapping(target = "remarkId", source = "remarkId"), // UUID 매핑
            @Mapping(target = "boardType", source = "boardId", qualifiedByName = "mapBoard"), // ✅ boardType으로 수정
            @Mapping(target = "member", source = "memberIdx", qualifiedByName = "mapMember"), // ✅ memberIdx로 수정
            @Mapping(target = "remarks", source = "remarks"),
            @Mapping(target = "createDate", expression = "java(java.time.LocalDate.now())"),
            @Mapping(target = "likeCount", constant = "0")
    })
    RemarkEntity toEntity(RemarkDTO dto);

    @Named("mapBoard")
    default BoardEntity mapBoard(String boardId) { // ✅ UUID 기반 변환
        if (boardId == null) return null;
        BoardEntity board = new BoardEntity();
        board.setBoardId(boardId); // String → UUID 변환
        return board;
    }

    @Named("mapMember")
    default MemberEntity mapMember(Integer memberIdx) { // ✅ memberIdx 기반 변환
        if (memberIdx == null) return null;
        MemberEntity member = new MemberEntity();
        member.setMemberIdx(memberIdx);
        return member;
    }
}

