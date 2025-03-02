package com.withus.project.repository.projects;

import com.withus.project.domain.projects.ContractEntity;
import com.withus.project.domain.projects.ContractStatus;
import com.withus.project.repository.AbstractRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Transactional(transactionManager = "transactionManager")
public class ContractRepositoryImpl extends AbstractRepository<ContractEntity> {

    public ContractRepositoryImpl(EntityManager entityManager) {
        super(entityManager);
    }

    @Override
    protected Class<ContractEntity> getEntityClass() {
        return ContractEntity.class;
    }

    @Override
    protected EntityManager getEntityManager() {
        return super.getEntityManager();
    }

    public List<ContractEntity> findByClientMemberIdAndStatus(String memberId, ContractStatus status) {
        String jpql = "SELECT c FROM ContractEntity c " +
                "WHERE c.project.client.member.id = :memberId " +  // <-- 여기서 member.id 활용
                "AND c.status = :status";

        TypedQuery<ContractEntity> query = getEntityManager().createQuery(jpql, ContractEntity.class);
        query.setParameter("memberId", memberId);
        query.setParameter("status", status);

        return query.getResultList();
    }

    public Optional<ContractEntity> findByUUID(String contractIdString) {
        try {
            // (1) String -> UUID 변환
            UUID uuid = UUID.fromString(contractIdString);

            // (2) JPQL로 contractId가 일치하는 ContractEntity 조회
            String jpql = "SELECT c FROM ContractEntity c WHERE c.contractId = :uuid";
            TypedQuery<ContractEntity> query = getEntityManager().createQuery(jpql, ContractEntity.class);
            query.setParameter("uuid", uuid);

            // (3) 결과 없으면 Optional.empty(), 있으면 Optional.of(...)
            List<ContractEntity> results = query.getResultList();
            if (results.isEmpty()) {
                return Optional.empty();
            }
            return Optional.of(results.get(0));

        } catch (IllegalArgumentException e) {
            // 만약 uuid 변환이 실패하면 (포맷이 틀린 경우 등) 빈 결과 반환
            return Optional.empty();
        }


    }



    public ContractEntity findByContractId(UUID uuid) {
        String jpql = "SELECT c FROM ContractEntity c WHERE c.contractId = :uuid";
        TypedQuery<ContractEntity> query = getEntityManager().createQuery(jpql, ContractEntity.class);
        query.setParameter("uuid", uuid);

        List<ContractEntity> results = query.getResultList();
        if (results.isEmpty()) {
            return null;
        }
        return results.get(0);
    }






}
