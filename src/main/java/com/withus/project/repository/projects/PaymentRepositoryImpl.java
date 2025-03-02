package com.withus.project.repository.projects;

import com.withus.project.domain.projects.PaymentEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class PaymentRepositoryImpl {

    @PersistenceContext
    private EntityManager entityManager;
    public PaymentEntity save(PaymentEntity paymentEntity){
        entityManager.persist(paymentEntity);
        return paymentEntity;
    }

    public PaymentEntity findById(Long paymentIdx) {
        return entityManager.find(PaymentEntity.class, paymentIdx);
    }

}
