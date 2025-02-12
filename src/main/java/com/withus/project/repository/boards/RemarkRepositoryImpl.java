package com.withus.project.repository.boards;

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

    /**
     * 특정 Board의 idx를 기반으로 모든 Remark 조회
     * @param boardId 게시판의 식별자
     * @return 해당 게시판의 모든 Remark 리스트
     */
    public List<RemarkEntity> findAllByBoardId(String boardId) {
        return entityManager.createQuery(
                        "SELECT r FROM RemarkEntity r WHERE r.boardType.boardId = :boardIdx ORDER BY r.depth, r.createDate",
                        RemarkEntity.class)
                .setParameter("boardId", boardId)
                .getResultList();
    }

    /**
     * 특정 idx를 기반으로 Remark 조회
     * @param remarkId 댓글 고유 식별자
     * @return Optional 형태의 RemarkEntity
     */
    public Optional<RemarkEntity> findByRemarkId(String remarkId) {
        return findById(remarkId);
    }

    /**
     * 특정 Member와 Board를 기반으로 모든 Remark 조회
     * @param memberId 회원 식별자
     * @param boardId 게시판 식별자
     * @return 해당 조건의 Remark 리스트
     */
    public List<RemarkEntity> findAllByMemberAndBoard(String id, String boardId) {
        return entityManager.createQuery(
                        "SELECT r FROM RemarkEntity r WHERE r.member.id = :id AND r.boardType.boardId = :boardId",
                        RemarkEntity.class)
                .setParameter("id", id)
                .setParameter("boardId", boardId)
                .getResultList();
    }

    /**
     * 특정 Remark 삭제
     * @param remarkId 삭제할 Remark의 식별자
     */
    public void deleteByRemarkIdx(String remarkId) {
        deleteById(remarkId);
    }

    /**
     * 특정 부모 댓글의 자식 댓글 리스트 조회
     * @param parentId 부모 댓글 고유 식별자
     * @return 자식 댓글 리스트
     */
    public List<RemarkEntity> findChildRemarks(String parentId) {
        return entityManager.createQuery(
                        "SELECT r FROM RemarkEntity r WHERE r.parentRemark.remarkId = :parentId ORDER BY r.createDate",
                        RemarkEntity.class)
                .setParameter("parentId", parentId)
                .getResultList();
    }
}

//수정완