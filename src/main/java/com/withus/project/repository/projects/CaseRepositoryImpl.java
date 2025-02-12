package com.withus.project.repository.projects;


import com.withus.project.domain.members.MyPageEntity;
import com.withus.project.domain.projects.CaseEntity;
import com.withus.project.repository.AbstractRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional(transactionManager = "transactionManager")
public class CaseRepositoryImpl extends AbstractRepository<CaseEntity> {

    public CaseRepositoryImpl(EntityManager entityManager) {
        super(entityManager);
    }

    @Override
    protected Class<CaseEntity> getEntityClass() {
        return CaseEntity.class;
    }

    @Override
    protected EntityManager getEntityManager() {
        return super.getEntityManager();
    }
}
