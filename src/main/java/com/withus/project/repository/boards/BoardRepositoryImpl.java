package com.withus.project.repository.boards;

import com.withus.project.domain.boards.BoardEntity;
import com.withus.project.domain.boards.BoardType;
import com.withus.project.domain.members.MemberEntity;
import com.withus.project.domain.members.PortfolioEntity;
import com.withus.project.domain.projects.ProjectEntity;
import com.withus.project.repository.AbstractRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional(transactionManager = "transactionManager")
public class BoardRepositoryImpl extends AbstractRepository<BoardEntity> {

    public BoardRepositoryImpl(EntityManager entityManager) {
        super(entityManager);
    }

    @Override
    protected Class<BoardEntity> getEntityClass() {
        return BoardEntity.class;
    }

    @Override
    protected EntityManager getEntityManager() {
        return super.getEntityManager();
    }
    // ✅ BoardType별 게시글 검색 및 페이징 처리
    public List<BoardEntity> findBoardsByTypeAndKeyword(BoardType boardType, String keyword) {
        String queryStr = "SELECT b FROM BoardEntity b WHERE b.boardType = :boardType";
        if (keyword != null && !keyword.isEmpty()) {
            queryStr += " AND (b.subject LIKE :keyword OR b.content LIKE :keyword)";
        }

        TypedQuery<BoardEntity> query = getEntityManager().createQuery(queryStr, BoardEntity.class)
                .setParameter("boardType", boardType);

        if (keyword != null && !keyword.isEmpty()) {
            query.setParameter("keyword", "%" + keyword + "%");
        }

        return query.getResultList();
    }

    public Optional<BoardEntity> findByBoardId(String boardId) {
        String queryStr = "SELECT b FROM BoardEntity b LEFT JOIN FETCH b.member WHERE b.boardId = :boardId";
        TypedQuery<BoardEntity> query = getEntityManager().createQuery(queryStr, BoardEntity.class)
                .setParameter("boardId", boardId);

        return query.getResultStream().findFirst();
    }

    public Optional<BoardEntity> findByBoardIdAndMember(String boardId, MemberEntity member) {
        String query = "SELECT b FROM BoardEntity b WHERE b.boardId = :boardId AND b.member = :member";
        return entityManager.createQuery(query, BoardEntity.class)
                .setParameter("boardId", boardId)
                .setParameter("member", member)
                .getResultStream()
                .findFirst();
    }
    @Transactional
    public void updateBoard(BoardEntity board) {
        entityManager.merge(board); // 기존 엔터티 업데이트
    }












}
