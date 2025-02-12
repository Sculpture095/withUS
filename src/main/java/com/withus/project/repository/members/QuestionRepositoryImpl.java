package com.withus.project.repository.members;

import com.withus.project.domain.members.QuestionEntity;
import com.withus.project.repository.AbstractRepository;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional(transactionManager = "transactionManager")
public class QuestionRepositoryImpl extends AbstractRepository<QuestionEntity> {

    public QuestionRepositoryImpl(EntityManager entityManager) {
        super(entityManager);
    }

    @Override
    protected Class<QuestionEntity> getEntityClass() {
        return QuestionEntity.class;
    }

    @Override
    protected EntityManager getEntityManager() {
        return super.getEntityManager();
    }
}

