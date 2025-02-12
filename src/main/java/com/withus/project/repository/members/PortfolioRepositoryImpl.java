package com.withus.project.repository.members;

import com.withus.project.domain.members.MemberEntity;
import com.withus.project.domain.members.PortfolioEntity;
import com.withus.project.repository.AbstractRepository;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional(transactionManager = "transactionManager")
public class PortfolioRepositoryImpl extends AbstractRepository<PortfolioEntity> {

    public PortfolioRepositoryImpl(EntityManager entityManager) {
        super(entityManager);
    }

    @Override
    protected Class<PortfolioEntity> getEntityClass() {
        return PortfolioEntity.class;
    }

    @Override
    protected EntityManager getEntityManager() {
        return super.getEntityManager();
    }
}

