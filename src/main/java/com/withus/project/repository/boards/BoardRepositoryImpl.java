package com.withus.project.repository.boards;

import com.withus.project.domain.boards.BoardEntity;
import com.withus.project.domain.members.PortfolioEntity;
import com.withus.project.repository.AbstractRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional(transactionManager = "transactionManager")
public class BoardRepositoryImpl extends AbstractRepository<BoardEntity> {

    public BoardRepositoryImpl(EntityManager entityManager) {
        super(entityManager);
    }

    @Override
    protected Class<BoardEntity> getEntityClass() {
        return BoardEntity.class;
    }

    @Override
    protected EntityManager getEntityManager() {
        return super.getEntityManager();
    }

    public List<BoardEntity> findAllPaged(int page, int size) {
        TypedQuery<BoardEntity> query = getEntityManager().createQuery(
                "SELECT b FROM BoardEntity b ORDER BY b.createDate DESC", BoardEntity.class);
        query.setFirstResult(page * size);
        query.setMaxResults(size);
        return query.getResultList();
    }
}
