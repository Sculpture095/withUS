package com.withus.project.repository.projects;


import com.withus.project.domain.members.MyPageEntity;
import com.withus.project.domain.projects.CaseEntity;
import com.withus.project.repository.AbstractRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
@Transactional(transactionManager = "transactionManager")
public class CaseRepositoryImpl {


    private final EntityManager em;

    public void save(CaseEntity caseEntity) {
        em.persist(caseEntity);
    }

    public CaseEntity findByCaseId(UUID caseId) {
        String jpql = "SELECT c FROM CaseEntity c WHERE c.caseId = :caseId";
        TypedQuery<CaseEntity> query = em.createQuery(jpql, CaseEntity.class)
                .setParameter("caseId", caseId);
        return query.getSingleResult();
    }

    public List<CaseEntity> findAll() {
        return em.createQuery("SELECT c FROM CaseEntity c", CaseEntity.class)
                .getResultList();
    }

    public void delete(CaseEntity entity) {
        em.remove(entity);
    }
}
