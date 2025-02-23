package com.withus.project.service;

import com.withus.project.domain.boards.BoardEntity;
import com.withus.project.domain.boards.RemarkEntity;
import com.withus.project.domain.dto.boards.RemarkDTO;
import com.withus.project.domain.members.MemberEntity;
import com.withus.project.mapper.boards.RemarkMapper;
import com.withus.project.repository.boards.BoardRepositoryImpl;
import com.withus.project.repository.boards.RemarkRepositoryImpl;
import com.withus.project.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RemarkService {

    private final RemarkRepositoryImpl remarkRepository;
    private final BoardRepositoryImpl boardRepository;
    private final MemberService memberService;

    // ✅ 특정 댓글 조회 (Optional 활용)
    public Optional<RemarkEntity> findByRemarkId(String remarkId) {
        return remarkRepository.findByRemarkId(remarkId);
    }

    // ✅ 특정 게시글의 댓글 조회 (대댓글 포함)
    @Transactional(readOnly = true)
    public List<RemarkEntity> getRemarksByBoardId(String boardId) {
        // 1) boardUuid(=외부 공개용 UUID)로 BoardEntity 찾기
        BoardEntity board = boardRepository.findByBoardId(boardId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "해당 boardId를 가진 게시글이 존재하지 않습니다: " + boardId));

        // 2) 그 board 엔티티로 remark 검색
        return remarkRepository.findAllByBoard(board);
    }


    // ✅ 특정 회원이 작성한 댓글 조회
    public List<RemarkEntity> getRemarksByMember(String memberId) {
        return remarkRepository.findAllByMemberId(memberId);
    }

    // ✅ 특정 회원이 특정 게시글에 작성한 댓글 조회
    public List<RemarkEntity> getRemarksByMemberAndBoard(String memberId, String boardId) {
        return remarkRepository.findAllByMemberAndBoard(memberId, boardId);
    }

    // ✅ 댓글 저장 (대댓글 포함)
    @Transactional
    public void saveRemark(String boardId, String memberId, String content, String parentRemarkId) {
        System.out.println("\n=== [DEBUG] 댓글 저장 시작 ===");
        System.out.println("boardId = " + boardId);
        System.out.println("memberId = " + memberId);
        System.out.println("content = " + content);
        System.out.println("parentRemarkId = " + parentRemarkId);

        MemberEntity member = Optional.ofNullable(memberService.findByMemberId(memberId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "해당 ID를 가진 회원이 존재하지 않습니다: " + memberId));

        BoardEntity board = boardRepository.findByBoardId(boardId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "해당 ID를 가진 게시글이 존재하지 않습니다: " + boardId));

        // 여기서 board를 찾지 못하면, 바로 404 + 메시지
        System.out.println("=== [DEBUG] board 찾기 성공! boardIdx = " + board.getBoardIdx()
                + ", boardId = " + board.getBoardId());

        RemarkEntity remark = new RemarkEntity();
        remark.setRemarks(content);
        remark.setMember(member);
        remark.setBoard(board);

        if (parentRemarkId != null) {
            RemarkEntity parent = remarkRepository.findByRemarkId(parentRemarkId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "해당 ID를 가진 부모 댓글이 존재하지 않습니다: " + parentRemarkId));
            remark.setParentRemark(parent);
            remark.setDepth(1);
            System.out.println("[DEBUG] 부모 댓글 존재. parentIdx=" + parent.getRemarkIdx()
                    + ", parentId=" + parent.getRemarkId());
        }

        remarkRepository.save(remark);
        System.out.println("=== [DEBUG] 댓글 저장 완료 ===");
        System.out.println("remarkIdx=" + remark.getRemarkIdx() + ", remarkId=" + remark.getRemarkId());
    }


    // ✅ 댓글 수정
    @Transactional
    public void updateRemark(String remarkId, String memberId, String content) {
        RemarkEntity remark = remarkRepository.findByRemarkId(remarkId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "댓글을 찾을 수 없습니다."));

        if (!remark.getMember().getId().equals(memberId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "댓글 수정 권한이 없습니다.");
        }

        remark.setRemarks(content);
        remarkRepository.save(remark);
    }

    // ✅ 댓글 삭제 (대댓글도 함께 삭제)
    @Transactional
    public void deleteRemark(String remarkId, String memberId) {
        RemarkEntity remark = remarkRepository.findByRemarkId(remarkId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "댓글을 찾을 수 없습니다."));

        if (!remark.getMember().getId().equals(memberId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "댓글 삭제 권한이 없습니다.");
        }

        remarkRepository.delete(remark);
    }

    // ✅ 댓글 좋아요 증가
    @Transactional
    public void increaseLikeCount(String remarkId) {
        RemarkEntity remark = remarkRepository.findByRemarkId(remarkId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "댓글을 찾을 수 없습니다."));

        remark.increaseLikeCount();
        remarkRepository.save(remark);
    }

    // ✅ 특정 부모 댓글의 모든 대댓글 조회
    public List<RemarkEntity> findChildRemarks(String parentId) {
        return remarkRepository.findChildRemarks(parentId);
    }


}
