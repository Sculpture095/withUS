package com.withus.project.mapper.boards;

import com.withus.project.domain.boards.BoardEntity;
import com.withus.project.domain.boards.RemarkEntity;
import com.withus.project.domain.dto.boards.RemarkDTO;
import com.withus.project.domain.members.MemberEntity;
import com.withus.project.mapper.DateTimeMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Mapper(componentModel = "spring", uses = {DateTimeMapper.class})
public interface RemarkMapper {


    @Mappings({
            @Mapping(target = "remarkIdx", ignore = true),
            @Mapping(target = "remarkId", source = "remarkId"), // UUID 매핑
            @Mapping(target = "boardId", source = "board.boardId"), // ✅ boardType을 사용해야 함
            @Mapping(target = "memberIdx", source = "member.memberIdx"), // ✅ memberIdx를 사용해야 함
            @Mapping(target = "remarks", source = "remarks"),
            @Mapping(target = "createDate", source = "createDate", qualifiedByName = "localDateTimeToString"), // ✅ DateTimeMapper의 메서드 사용
            @Mapping(target = "likeCount", source = "likeCount"),
            @Mapping(target = "parentRemarkId", source = "parentRemark.remarkId", defaultValue = "null") // ✅ 대댓글 지원
    })
    RemarkDTO toDTO(RemarkEntity entity);

    @Mappings({
            @Mapping(target = "remarkIdx", ignore = true),
            @Mapping(target = "remarkId", source = "remarkId"), // UUID 매핑
            @Mapping(target = "board", source = "boardId", qualifiedByName = "mapBoard"), // ✅ boardType으로 수정
            @Mapping(target = "member", source = "memberIdx", qualifiedByName = "mapMember"), // ✅ memberIdx로 수정
            @Mapping(target = "remarks", source = "remarks"),
            @Mapping(target = "parentRemark", source = "parentRemarkId", qualifiedByName = "mapParentRemark"), // ✅ 부모 댓글 매핑
            @Mapping(target = "createDate", source = "createDate", qualifiedByName = "stringToLocalDateTime"), // ✅ DateTimeMapper의 메서드 사용
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

    // ✅ `parentRemarkId` → `RemarkEntity` 변환 (대댓글 지원)
    @Named("mapParentRemark")
    default RemarkEntity mapParentRemark(String parentRemarkId) {
        if (parentRemarkId == null) return null;
        RemarkEntity parentRemark = new RemarkEntity();
        parentRemark.setRemarkId(parentRemarkId);
        return parentRemark;
    }


}

