package com.withus.project.repository.projects;

import com.withus.project.domain.projects.ProjectEntity;
import com.withus.project.domain.projects.SelectProjectEntity;
import com.withus.project.repository.AbstractRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

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

    /**
     * 1) projectId (String → UUID) 로 ProjectEntity 먼저 조회
     * 2) SelectProjectEntity 조회: sp.project = :project
     */
    public List<SelectProjectEntity> findByProjectId(String projectIdString) {
        // 1) 문자열 → UUID
        UUID uuid = UUID.fromString(projectIdString);

        // 2) ProjectEntity 조회
        ProjectEntity project = entityManager.createQuery(
                        "SELECT p FROM ProjectEntity p WHERE p.projectId = :uuid",
                        ProjectEntity.class
                )
                .setParameter("uuid", uuid)
                .getSingleResult();

        // 3) SelectProjectEntity (연관관계) 조회
        return entityManager.createQuery(
                        "SELECT sp FROM SelectProjectEntity sp " +
                                "WHERE sp.project = :project",
                        SelectProjectEntity.class
                )
                .setParameter("project", project)
                .getResultList();
    }

    public List<SelectProjectEntity> findByMemberId(String memberId) {
        return entityManager.createQuery(
                        "SELECT sp FROM SelectProjectEntity sp " +
                                "WHERE sp.partner.member.id = :memberId",
                        SelectProjectEntity.class
                )
                .setParameter("memberId", memberId)
                .getResultList();
    }

    /**
     * 1) projectId (String → UUID)로 ProjectEntity 조회
     * 2) sp.project = :project 로 조회
     */
    public SelectProjectEntity findByProjectIdAndMemberId(String projectIdString, String memberId) {
        // 1) ProjectEntity 조회
        UUID uuid = UUID.fromString(projectIdString);
        ProjectEntity project = entityManager.createQuery(
                        "SELECT p FROM ProjectEntity p WHERE p.projectId = :uuid",
                        ProjectEntity.class
                )
                .setParameter("uuid", uuid)
                .getSingleResult();

        // 2) SelectProjectEntity 조회
        return entityManager.createQuery(
                        "SELECT sp FROM SelectProjectEntity sp " +
                                "WHERE sp.project = :project AND sp.partner.member.id = :memberId",
                        SelectProjectEntity.class
                )
                .setParameter("project", project)
                .setParameter("memberId", memberId)
                .getSingleResult();
    }

    public void deleteByProjectIdAndMemberId(String projectIdString, String memberId) {
        SelectProjectEntity entity = findByProjectIdAndMemberId(projectIdString, memberId);
        if (entity == null) {
            throw new EntityNotFoundException("해당 프로젝트 혹은 지원 정보를 찾을 수 없습니다.");
        }
        entityManager.remove(entity);
    }

    public boolean existsByProjectIdAndMemberId(String projectIdString, String memberId) {
        // 1) UUID 변환 후 ProjectEntity 조회
        UUID uuid = UUID.fromString(projectIdString);
        ProjectEntity project = entityManager.createQuery(
                        "SELECT p FROM ProjectEntity p WHERE p.projectId = :uuid",
                        ProjectEntity.class
                )
                .setParameter("uuid", uuid)
                .getSingleResult();

        // 2) SelectProjectEntity 개수 확인
        Long count = entityManager.createQuery(
                        "SELECT COUNT(sp) FROM SelectProjectEntity sp " +
                                "WHERE sp.project = :project AND sp.partner.member.id = :memberId",
                        Long.class
                )
                .setParameter("project", project)
                .setParameter("memberId", memberId)
                .getSingleResult();

        return (count > 0);
    }

    /**
     * 지원자 ID 목록만 조회하는 경우에도 2단계 조회
     */
    public List<String> findMemberIdsByProjectId(String projectIdString) {
        // 1) ProjectEntity 조회
        UUID uuid = UUID.fromString(projectIdString);
        ProjectEntity project = entityManager.createQuery(
                        "SELECT p FROM ProjectEntity p WHERE p.projectId = :uuid",
                        ProjectEntity.class
                )
                .setParameter("uuid", uuid)
                .getSingleResult();

        // 2) SelectProjectEntity에서 partner.member.id 목록 추출
        return entityManager.createQuery(
                        "SELECT sp.partner.member.id FROM SelectProjectEntity sp " +
                                "WHERE sp.project = :project",
                        String.class
                )
                .setParameter("project", project)
                .getResultList();
    }
}
