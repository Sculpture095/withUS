package com.withus.project.repository.members;

import com.withus.project.domain.projects.PaymentEntity;
import com.withus.project.repository.AbstractRepository;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(transactionManager = "transactionManager")
public class PaymentRepositoryImpl extends AbstractRepository<PaymentEntity> {

    public PaymentRepositoryImpl(EntityManager entityManager) {
        super(entityManager);
    }

    @Override
    protected Class<PaymentEntity> getEntityClass() {
        return PaymentEntity.class;
    }

    @Override
    protected EntityManager getEntityManager() {
        return super.getEntityManager();
    }
}
