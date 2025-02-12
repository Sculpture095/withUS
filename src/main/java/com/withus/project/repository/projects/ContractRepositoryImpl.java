package com.withus.project.repository.projects;

import com.withus.project.domain.projects.ContractEntity;
import com.withus.project.repository.AbstractRepository;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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
}
