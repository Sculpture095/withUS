package com.withus.project.repository.projects;

import com.withus.project.domain.projects.SelectProjectEntity;
import com.withus.project.repository.AbstractRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(transactionManager = "transactionManager")
public class SelectProjectRepositoryImpl extends AbstractRepository<SelectProjectEntity> {

    public SelectProjectRepositoryImpl(EntityManager entityManager) {
        super(entityManager);
    }

    @Override
    protected Class<SelectProjectEntity> getEntityClass() {
        return SelectProjectEntity.class;
    }

    public List<SelectProjectEntity> findByProjectId(String projectId) {
        return entityManager.createQuery(
                        "SELECT sp FROM SelectProjectEntity sp WHERE sp.project.projectId = :projectId", SelectProjectEntity.class)
                .setParameter("projectId", projectId)
                .getResultList();
    }

    /**
     * id를 기반으로 SelectProject 조회
     *
     * @param id 사용자의 ID
     * @return 해당 사용자의 SelectProjectEntity 리스트
     */
    public List<SelectProjectEntity> findByMemberId(String id) {
        return entityManager.createQuery(
                        "SELECT sp FROM SelectProjectEntity sp " +
                                "WHERE sp.partner.member.id = :id", SelectProjectEntity.class)
                .setParameter("id", id)
                .getResultList();
    }


    public SelectProjectEntity findByProjectIdAndMemberId(String projectId, String id) {
        return entityManager.createQuery(
                        "SELECT sp FROM SelectProjectEntity sp " +
                                "WHERE sp.project.projectId = :projectId AND sp.partner.member.id = :id", SelectProjectEntity.class)
                .setParameter("projectId", projectId)
                .setParameter("id", id)
                .getSingleResult();
    }


    public void deleteByProjectIdAndMemberId(String projectId, String id) {
        SelectProjectEntity entity = findByProjectIdAndMemberId(projectId, id);
        if (entity == null) {
            throw new EntityNotFoundException("선택한 프로젝트를 찾을 수 없습니다: projectIdx=" + projectId + ", id=" + id);
        }
        entityManager.remove(entity);
    }

    public boolean existsByProjectIdAndMemberId(String projectId, String id) {
        Long count = entityManager.createQuery(
                        "SELECT COUNT(sp) FROM SelectProjectEntity sp " +
                                "WHERE sp.project.projectId = :projectId AND sp.partner.member.id = :id", Long.class)
                .setParameter("projectId", projectId)
                .setParameter("id", id)
                .getSingleResult();
        return count > 0;
    }

    

}
