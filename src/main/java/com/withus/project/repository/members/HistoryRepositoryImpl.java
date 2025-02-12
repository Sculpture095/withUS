package com.withus.project.repository.members;

import com.withus.project.domain.members.HistoryEntity;
import com.withus.project.repository.AbstractRepository;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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
    public List<HistoryEntity> findAllByMemberId(String id) {
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

    /**
     * 특정 경력 번호로 단일 경력 조회
     *
     * @param historyIdx 경력 번호
     * @return 경력 엔터티 (Optional)
     */
    public Optional<HistoryEntity> findByHistoryId(String historyId) {
        return findById(historyId);
    }
}

//수정완