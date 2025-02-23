package com.withus.project.repository.boards;

import com.withus.project.domain.boards.BoardEntity;
import com.withus.project.domain.boards.RemarkEntity;
import com.withus.project.repository.AbstractRepository;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional(transactionManager = "transactionManager")
public class RemarkRepositoryImpl extends AbstractRepository<RemarkEntity> {

    public RemarkRepositoryImpl(EntityManager entityManager) {
        super(entityManager);
    }

    @Override
    protected Class<RemarkEntity> getEntityClass() {
        return RemarkEntity.class;
    }

    // ✅ 특정 게시글의 모든 댓글 조회
    public List<RemarkEntity> findAllByBoard(BoardEntity board) {
        return entityManager.createQuery(
                        "SELECT r FROM RemarkEntity r WHERE r.board = :board ORDER BY r.depth, r.createDate",
                        RemarkEntity.class)
                .setParameter("board", board)
                .getResultList();
    }

    // ✅ 특정 회원이 작성한 모든 댓글 조회
    public List<RemarkEntity> findAllByMemberId(String memberId) {
        return entityManager.createQuery(
                        "SELECT r FROM RemarkEntity r WHERE r.member.id = :memberId ORDER BY r.createDate DESC",
                        RemarkEntity.class)
                .setParameter("memberId", memberId)
                .getResultList();
    }

    // ✅ 특정 회원이 특정 게시글에서 작성한 모든 댓글 조회
    public List<RemarkEntity> findAllByMemberAndBoard(String memberId, String boardId) {
        return entityManager.createQuery(
                        "SELECT r FROM RemarkEntity r WHERE r.member.id = :memberId AND r.board.boardId = :boardId",
                        RemarkEntity.class)
                .setParameter("memberId", memberId)
                .setParameter("boardId", boardId)
                .getResultList();
    }

    // ✅ 특정 댓글 조회
    public Optional<RemarkEntity> findByRemarkId(String remarkId) {
        return entityManager.createQuery(
                        "SELECT r FROM RemarkEntity r WHERE r.remarkId = :remarkId",
                        RemarkEntity.class)
                .setParameter("remarkId", remarkId)
                .getResultStream()
                .findFirst();
    }

    // ✅ 특정 부모 댓글의 자식 댓글 리스트 조회
    public List<RemarkEntity> findChildRemarks(String parentId) {
        return entityManager.createQuery(
                        "SELECT r FROM RemarkEntity r WHERE r.parentRemark.remarkId = :parentId ORDER BY r.createDate",
                        RemarkEntity.class)
                .setParameter("parentId", parentId)
                .getResultList();
    }
}

//수정완