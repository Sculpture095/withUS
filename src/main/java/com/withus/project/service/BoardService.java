package com.withus.project.service;

import com.withus.project.domain.boards.BoardEntity;
import com.withus.project.domain.dto.PageResponse;
import com.withus.project.domain.dto.boards.BoardDTO;
import com.withus.project.mapper.boards.BoardMapper;
import com.withus.project.repository.boards.BoardRepositoryImpl;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepositoryImpl boardRepository;
    private final BoardMapper boardMapper;
    private final EntityManager entityManager;

    /**
     * 게시글 등록
     * @param dto BoardDTO (게시글 데이터)
     * @return 등록된 게시글의 BoardDTO
     */
    @Transactional
    public BoardDTO registerBoard(BoardDTO dto) {
        if (dto.getSubject() == null || dto.getSubject().isEmpty()) {
            throw new IllegalArgumentException("게시글 제목은 필수 입력 항목입니다.");
        }
        if (dto.getContent() == null || dto.getContent().isEmpty()) {
            throw new IllegalArgumentException("게시글 내용은 필수 입력 항목입니다.");
        }

        BoardEntity entity = boardMapper.toEntity(dto);
        BoardEntity savedEntity = boardRepository.save(entity);
        return boardMapper.toDTO(savedEntity);
    }

    /**
     * 게시글 수정
     * @param dto BoardDTO (수정 데이터)
     * @param boardId 수정할 게시글의 IDX
     * @return 수정된 게시글의 BoardDTO
     */
    @Transactional
    public BoardDTO updateBoard(BoardDTO dto, String boardId) {
        BoardEntity existingEntity = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글을 찾을 수 없습니다. IDX: " + boardId));

        if (dto.getSubject() == null || dto.getSubject().isEmpty()) {
            throw new IllegalArgumentException("게시글 제목은 필수 입력 항목입니다.");
        }
        if (dto.getContent() == null || dto.getContent().isEmpty()) {
            throw new IllegalArgumentException("게시글 내용은 필수 입력 항목입니다.");
        }

        existingEntity.setSubject(dto.getSubject());
        existingEntity.setContent(dto.getContent());

        if (dto.getFilePath() != null && !dto.getFilePath().isEmpty()) {
            existingEntity.setFilePath(dto.getFilePath());
        }

        BoardEntity updatedEntity = boardRepository.save(existingEntity);
        return boardMapper.toDTO(updatedEntity);
    }

    /**
     * 게시글 조회수 증가
     * @param boardId 조회수 증가 대상 게시글의 IDX
     */
    @Transactional
    public void incrementViewCount(String boardId) {
        BoardEntity entity = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글을 찾을 수 없습니다. IDX: " + boardId));
        entity.setViewCount(entity.getViewCount() + 1);
        boardRepository.save(entity);
    }

    /**
     * 게시글 추천수 증가
     * @param boardId 추천수 증가 대상 게시글의 IDX
     */
    @Transactional
    public void incrementLikeCount(String boardId) {
        BoardEntity entity = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글을 찾을 수 없습니다. IDX: " + boardId));
        entity.setLikeCount(entity.getLikeCount() + 1);
        boardRepository.save(entity);
    }

    /**
     * 페이징 처리된 게시글 조회
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지 크기
     * @return 페이징 처리된 게시글 리스트
     */
    @Transactional(readOnly = true)
    public PageResponse<BoardDTO> getBoardsByPage(int page, int size) {
        // 총 게시글 수를 한 번만 조회
        long totalElements = entityManager.createQuery(
                "SELECT COUNT(b) FROM BoardEntity b", Long.class).getSingleResult();

        TypedQuery<BoardEntity> query = entityManager.createQuery(
                "SELECT b FROM BoardEntity b ORDER BY b.createDate DESC", BoardEntity.class);
        query.setFirstResult(page * size);
        query.setMaxResults(size);

        List<BoardEntity> boardEntities = query.getResultList();
        List<BoardDTO> content = boardEntities.stream()
                .map(boardMapper::toDTO)
                .collect(Collectors.toList());

        int totalPages = (int) Math.ceil((double) totalElements / size);
        return PageResponse.<BoardDTO>builder()
                .content(content)
                .page(page)
                .size(size)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .build();
    }

    /**
     * 게시글 단일 조회 및 댓글 포함
     * @param boardId 조회할 게시글의 IDX
     * @return 조회된 게시글의 BoardDTO
     */
    @Transactional(readOnly = true)
    public BoardDTO getBoardWithComments(String boardId) {
        BoardEntity board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글을 찾을 수 없습니다. IDX: " + boardId));
        BoardDTO boardDTO = boardMapper.toDTO(board);
        // 댓글 매핑 추가 로직 필요
        return boardDTO;
    }

    /**
     * 게시글 검색
     * @param keyword 검색 키워드
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지 크기
     * @return 검색된 게시글 리스트
     */
    @Transactional(readOnly = true)
    public PageResponse<BoardDTO> searchBoards(String keyword, int page, int size) {
        String queryStr = "SELECT b FROM BoardEntity b WHERE b.subject LIKE :keyword OR b.content LIKE :keyword ORDER BY b.createDate DESC";
        TypedQuery<BoardEntity> query = entityManager.createQuery(queryStr, BoardEntity.class);
        query.setParameter("keyword", "%" + keyword + "%");
        query.setFirstResult(page * size);
        query.setMaxResults(size);

        List<BoardEntity> boardEntities = query.getResultList();
        long totalElements = entityManager.createQuery(
                        "SELECT COUNT(b) FROM BoardEntity b WHERE b.subject LIKE :keyword OR b.content LIKE :keyword", Long.class)
                .setParameter("keyword", "%" + keyword + "%").getSingleResult();

        List<BoardDTO> content = boardEntities.stream()
                .map(boardMapper::toDTO)
                .collect(Collectors.toList());

        int totalPages = (int) Math.ceil((double) totalElements / size);
        return PageResponse.<BoardDTO>builder()
                .content(content)
                .page(page)
                .size(size)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .build();
    }
}