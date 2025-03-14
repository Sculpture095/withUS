package com.withus.project.service;

import com.withus.project.domain.boards.BoardEntity;
import com.withus.project.domain.boards.BoardType;
import com.withus.project.repository.boards.BoardRepositoryImpl;
import com.withus.project.service.other.FileUploadService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepositoryImpl boardRepository;
    private final EntityManager entityManager;
    private final FileUploadService fileUploadService;

    // ✅ 게시판별 게시글 조회 (페이징 및 검색 적용)
    public Page<BoardEntity> getBoardsByType(BoardType boardType, String keyword, Pageable pageable) {
        String queryStr = "SELECT b FROM BoardEntity b LEFT JOIN FETCH b.member WHERE b.boardType = :boardType";

        if (keyword != null && !keyword.isEmpty()) {
            queryStr += " AND (b.subject LIKE :keyword OR b.content LIKE :keyword)";
        }

        TypedQuery<BoardEntity> query = entityManager.createQuery(queryStr, BoardEntity.class)
                .setParameter("boardType", boardType);

        if (keyword != null && !keyword.isEmpty()) {
            query.setParameter("keyword", "%" + keyword + "%");
        }

        List<BoardEntity> resultList = query.getResultList();
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), resultList.size());
        List<BoardEntity> pagedList = resultList.subList(start, end);

        return new PageImpl<>(pagedList, pageable, resultList.size());
    }

    public BoardEntity getBoardByBoardId(String boardId) {
        return boardRepository.findByBoardId(boardId).orElse(null);
    }

    @Transactional
    public void saveBoard(BoardEntity board, MultipartFile file) {
        System.out.println("=== [DEBUG] 게시글 저장 시작 ===");
        System.out.println("board.subject = " + board.getSubject());
        System.out.println("board.content = " + board.getContent());
        // 로그인한 사용자, boardType 등도 찍어볼 수 있음

        try {
            if (file != null && !file.isEmpty()) {
                String filePath = fileUploadService.storeFile(file);
                board.setFilePath(filePath);
                System.out.println("파일이 성공적으로 저장됨: " + filePath);
            } else {
                board.setFilePath(null);
                System.out.println("파일 없음 → 게시글만 저장");
            }

            boardRepository.save(board);
            System.out.println("=== [DEBUG] 게시글 저장 완료 ===");
            System.out.println("boardIdx(물리 PK) = " + board.getBoardIdx());
            System.out.println("boardId(문자열 UUID) = " + board.getBoardId());

        } catch (Exception e) {
            System.out.println("게시글 저장 중 오류 발생: " + e.getMessage());
            throw new RuntimeException("게시글 저장 실패: " + e.getMessage());
        }
    }




    @Transactional
    public void updateBoard(String boardId, String subject, String content, MultipartFile file, String memberId) {
        BoardEntity board = boardRepository.findByBoardId(boardId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."));

        // 수정 권한 체크
        if (!board.getMember().getId().equals(memberId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "게시글 수정 권한이 없습니다.");
        }

        // 기존 제목/내용 업데이트
        board.setSubject(subject);
        board.setContent(content);

        // 기존 파일 경로
        String oldFilePath = board.getFilePath();

        // (A) 업로드된 파일이 없는 경우 => 기존 파일 삭제
        if (file == null || file.isEmpty()) {
            // 기존에 저장된 파일이 있다면, 오브젝트 스토리지에서 삭제
            if (oldFilePath != null) {
                fileUploadService.deleteFile(oldFilePath);
            }
            // DB상 파일 경로도 null
            board.setFilePath(null);

        } else {
            // (B) 새 파일이 업로드됨 => 기존 파일이 있다면 먼저 삭제
            if (oldFilePath != null) {
                fileUploadService.deleteFile(oldFilePath);
            }
            // 새 파일 업로드
            String newFilePath = fileUploadService.storeFile(file);
            board.setFilePath(newFilePath);
        }

        // 변경사항 저장
        boardRepository.save(board);
    }

    @Transactional
    public void deleteBoard(String boardId) {
        BoardEntity board = boardRepository.findByBoardId(boardId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."));

        // 실제 S3(오브젝트 스토리지) 파일도 삭제
        String filePath = board.getFilePath();
        if (filePath != null) {
            fileUploadService.deleteFile(filePath);
        }
        boardRepository.delete(board); // ✅ 게시글 삭제
    }

    // ✅ 조회수 증가
    @Transactional
    public void increaseViewCount(String boardId) {
        BoardEntity board = boardRepository.findByBoardId(boardId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."));
        board.setViewCount(board.getViewCount() + 1);
        boardRepository.save(board);
    }

    // ✅ 추천수 증가
    @Transactional
    public void increaseLikeCount(String boardId) {
        BoardEntity board = boardRepository.findByBoardId(boardId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."));
        board.setLikeCount(board.getLikeCount() + 1);
        boardRepository.save(board);
    }





}