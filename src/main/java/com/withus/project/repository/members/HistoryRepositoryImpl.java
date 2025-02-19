package com.withus.project.repository.members;

import com.withus.project.domain.members.HistoryEntity;
import com.withus.project.repository.AbstractRepository;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Transactional(transactionManager = "transactionManager")
public class HistoryRepositoryImpl extends AbstractRepository<HistoryEntity> {

    public HistoryRepositoryImpl(EntityManager entityManager) {
        super(entityManager);
    }

    @Override
    protected Class<HistoryEntity> getEntityClass() {
        return HistoryEntity.class;
    }

    /**
     * 특정 Member ID를 기반으로 모든 경력 조회
     *
     * @param id 사용자 ID
     * @return 해당 사용자의 모든 경력 리스트
     */
    public List<HistoryEntity> findAllHistoryByMemberId(String id) {
        return entityManager.createQuery(
                        "SELECT h FROM HistoryEntity h WHERE h.partner.partnerIdx IN (" +
                                "  SELECT p.partnerIdx FROM PartnerEntity p " +
                                "  WHERE p.member.memberIdx = (" +
                                "    SELECT m.memberIdx FROM MemberEntity m WHERE m.id = :id" +
                                "  )" +
                                ")",
                        HistoryEntity.class)
                .setParameter("id", id)
                .getResultList();
    }

    // 경력 추가
    public void saveHistory(HistoryEntity history) {
        entityManager.persist(history);
    }

    // 특정 사용자 ID와 historyId를 기반으로 경력 삭제
    public void deleteHistoryByMemberId(String memberId, String historyId) {
        Optional<HistoryEntity> history = entityManager.createQuery(
                        "SELECT h FROM HistoryEntity h WHERE h.historyId = :historyId AND h.partner.partnerIdx IN (" +
                                "SELECT p.partnerIdx FROM PartnerEntity p WHERE p.member.memberIdx = " +
                                "(SELECT m.memberIdx FROM MemberEntity m WHERE m.id = :memberId))",
                        HistoryEntity.class)
                .setParameter("historyId", historyId)
                .setParameter("memberId", memberId)
                .getResultStream()
                .findFirst();

        history.ifPresent(entityManager::remove);
    }
    }





//수정완