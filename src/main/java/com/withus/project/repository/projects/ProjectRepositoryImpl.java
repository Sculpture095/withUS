package com.withus.project.repository.projects;


import com.withus.project.domain.members.PartnerEntity;
import com.withus.project.domain.projects.ProjectEntity;
import com.withus.project.domain.projects.ProjectStatus;
import com.withus.project.domain.projects.SelectProjectEntity;
import com.withus.project.dto.projects.CompletedProjectDTO;
import com.withus.project.repository.AbstractRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
@Transactional(transactionManager = "transactionManager")
public class ProjectRepositoryImpl extends AbstractRepository<ProjectEntity> {

    public ProjectRepositoryImpl(EntityManager entityManager) {
        super(entityManager);
    }

    @Override
    protected Class<ProjectEntity> getEntityClass() {
        return ProjectEntity.class;
    }

    @Override
    protected EntityManager getEntityManager() {
        return super.getEntityManager();
    }


    public List<ProjectEntity> findAllProjects() {
        return findAll(); // AbstractRepository에서 제공하는 findAll() 사용
    }

    // ✅ UUID 기반 프로젝트 조회 메서드 추가
    public Optional<ProjectEntity> findByUUID(UUID projectId) {
        return entityManager.createQuery(
                        "SELECT p FROM ProjectEntity p WHERE p.projectId = :projectId", ProjectEntity.class)
                .setParameter("projectId", projectId)
                .getResultStream()
                .findFirst();
    }




    /**
     * 특정 idx를 기반으로 ProjectEntity 전체 업데이트
     *
     * @param id 업데이트할 엔터티의 식별자
     * @param updatedEntity 업데이트된 ProjectEntity
     */
    public void update(String id, ProjectEntity updatedEntity) {
        super.update(id, updatedEntity);
    }

    /**
     * 특정 idx를 기반으로 ProjectEntity 일부 필드 업데이트
     *
     * @param id 업데이트할 엔터티의 식별자
     * @param fields 업데이트할 필드와 값의 맵
     */
    public void partialUpdate(String id, Map<String, Object> fields) {
        super.partialUpdate(id, fields);
    }


    public List<ProjectEntity> findProjectByMemberId(String id) {
        return entityManager.createQuery(
                        "SELECT p FROM ProjectEntity p " +
                                "WHERE p.client.clientIdx = (" +
                                "  SELECT c.clientIdx FROM ClientEntity c " +
                                "  WHERE c.member.memberIdx = (" +
                                "    SELECT m.memberIdx FROM MemberEntity m WHERE m.id = :id" +
                                "  )" +
                                ")",
                        ProjectEntity.class)
                .setParameter("id", id)
                .getResultList();
    }

    /**
     * 특정 조건으로 프로젝트 검색
     *
     * @param keyword 키워드 (프로젝트 이름/정보 검색)
     * @param status 프로젝트 상태
     * @param minAmount 최소 금액
     * @param maxAmount 최대 금액
     * @return 조건에 맞는 프로젝트 리스트
     */
    public List<ProjectEntity> searchProjects(String keyword, ProjectStatus status, Double minAmount, Double maxAmount) {
        StringBuilder jpql = new StringBuilder("SELECT p FROM ProjectEntity p WHERE 1=1");

        if (keyword != null && !keyword.isEmpty()) {
            jpql.append(" AND (p.projectName LIKE :keyword OR p.projectInfo LIKE :keyword)");
        }
        if (status != null) {
            jpql.append(" AND p.proStatement = :status");
        }
        if (minAmount != null) {
            jpql.append(" AND p.amount >= :minAmount");
        }
        if (maxAmount != null) {
            jpql.append(" AND p.amount <= :maxAmount");
        }

        var query = entityManager.createQuery(jpql.toString(), ProjectEntity.class);

        if (keyword != null && !keyword.isEmpty()) {
            query.setParameter("keyword", "%" + keyword + "%");
        }
        if (status != null) {
            query.setParameter("status", status);
        }
        if (minAmount != null) {
            query.setParameter("minAmount", minAmount);
        }
        if (maxAmount != null) {
            query.setParameter("maxAmount", maxAmount);
        }

        return query.getResultList();
    }


    public List<ProjectEntity> findAllProjects(int offset, int limit) {
        String jpql = "SELECT p FROM ProjectEntity p ORDER BY p.registrationDate DESC";
        TypedQuery<ProjectEntity> query = entityManager.createQuery(jpql, ProjectEntity.class);
        query.setFirstResult(offset);
        query.setMaxResults(limit);
        return query.getResultList();
    }
    public long countAllPartners(){
        String jpql = "SELECT COUNT(p) FROM ProjectEntity p";
        return entityManager.createQuery(jpql, Long.class)
                .getSingleResult();
    }

    /**
     * 등록일자 순 프로젝트 조회
     *
     * @param ascending 정렬 방향 (true: 오름차순, false: 내림차순)
     * @return 등록일자 순 정렬된 프로젝트 리스트
     */
    public List<ProjectEntity> findByStartDate(boolean ascending) {
        String orderBy = ascending ? "ASC" : "DESC";
        return entityManager.createQuery(
                        "SELECT p FROM ProjectEntity p ORDER BY p.startDate " + orderBy, ProjectEntity.class)
                .getResultList();
    }

    /**
     * 지원자 수 적은 순 프로젝트 조회
     *
     * @return 지원자 수가 적은 순으로 정렬된 프로젝트 리스트
     */
    public List<ProjectEntity> findByTeamSize() {
        return entityManager.createQuery(
                        "SELECT p FROM ProjectEntity p ORDER BY p.teamSize ASC", ProjectEntity.class)
                .getResultList();
    }

    /**
     * 금액 순 프로젝트 조회
     *
     * @param ascending 정렬 방향 (true: 오름차순, false: 내림차순)
     * @return 금액 순 정렬된 프로젝트 리스트
     */
    public List<ProjectEntity> findByAmount(boolean ascending) {
        String orderBy = ascending ? "ASC" : "DESC";
        return entityManager.createQuery(
                        "SELECT p FROM ProjectEntity p ORDER BY p.amount " + orderBy, ProjectEntity.class)
                .getResultList();
    }

    /**
     * 상태 기반 프로젝트 조회
     *
     * @param status 프로젝트 상태
     * @return 해당 상태의 프로젝트 리스트
     */
    public List<ProjectEntity> findByStatus(ProjectStatus status) {
        return entityManager.createQuery(
                        "SELECT p FROM ProjectEntity p WHERE p.proStatement = :status", ProjectEntity.class)
                .setParameter("status", status)
                .getResultList();
    }

    /**
     * 전체 프로젝트 개수 조회
     *
     * @return 전체 프로젝트 개수
     */
    public long count() {
        return entityManager.createQuery(
                        "SELECT COUNT(p) FROM ProjectEntity p", Long.class)
                .getSingleResult();
    }
    //추가



    /**
     * 특정 프로젝트와 사용자 ID로 참여 정보 조회
     *
     * @param projectId 프로젝트 식별 번호
     * @param id         사용자 ID
     * @return SelectProjectEntity
     */
    public SelectProjectEntity findSelectProjectByProjectIdAndMemberId(String projectId, String id) {
        return entityManager.createQuery(
                        "SELECT sp FROM SelectProjectEntity sp " +
                                "WHERE sp.project.projectId = :projectId AND sp.partner.member.id = :id", SelectProjectEntity.class)
                .setParameter("projectId", UUID.fromString(projectId))
                .setParameter("id", id)
                .getSingleResult();
    }

    /**
     * 특정 프로젝트와 사용자 ID로 참여 정보 삭제
     *
     * @param projectId 프로젝트 식별 번호
     * @param id         사용자 ID
     */
    public void deleteSelectProjectByProjectIdAndMemberId(String projectId, String id) {
        SelectProjectEntity entity = findSelectProjectByProjectIdAndMemberId(projectId, id);
        if (entity == null) {
            throw new EntityNotFoundException("선택한 프로젝트를 찾을 수 없습니다: projectIdx=" + projectId + ", id=" + id);
        }
        entityManager.remove(entity);
    }

    /**
     * 특정 프로젝트에 참여한 파트너 ID 리스트 조회
     *
     * @param projectIdx 프로젝트 식별 번호
     * @return 참여한 파트너들의 사용자 ID 리스트
     */

    /**
     * 사용자 ID를 기반으로 PartnerEntity 조회
     *
     * @param id 사용자 ID
     * @return PartnerEntity
     */
    public PartnerEntity findPartnerByMemberId(String id) {
        return entityManager.createQuery(
                        "SELECT p FROM PartnerEntity p WHERE p.member.id = :id", PartnerEntity.class)
                .setParameter("id", id)
                .getSingleResult();
    }

    public List<ProjectEntity> findByCompletionStatus(boolean isCompleted) {
        return entityManager.createQuery(
                        "SELECT p FROM ProjectEntity p WHERE p.isCompleted = :isCompleted", ProjectEntity.class)
                .setParameter("isCompleted", isCompleted)
                .getResultList();
    }

    //추가

    //모집 마감일 전 조회
    public List<ProjectEntity> findByClosingDateBefore(LocalDate date) {
        return entityManager.createQuery(
                        "SELECT p FROM ProjectEntity p WHERE p.closingDate < :date", ProjectEntity.class)
                .setParameter("date", date)
                .getResultList();
    }
    //모집 마감일 이후 조회
    public List<ProjectEntity> findByClosingDateAfter(LocalDate date) {
        return entityManager.createQuery(
                        "SELECT p FROM ProjectEntity p WHERE p.closingDate > :date", ProjectEntity.class)
                .setParameter("date", date)
                .getResultList();
    }

    //오늘 마감되는 프로젝트를 조회
    public List<ProjectEntity> findByClosingDateToday() {
        LocalDate today = LocalDate.now();
        return entityManager.createQuery(
                        "SELECT p FROM ProjectEntity p WHERE p.closingDate = :today", ProjectEntity.class)
                .setParameter("today", today)
                .getResultList();
    }

    public List<String> findMemberIdsByProjectId(String projectId) {
        return entityManager.createQuery(
                        "SELECT sp.partner.member.id FROM SelectProjectEntity sp WHERE sp.project.projectId = :projectId", String.class)
                .setParameter("projectId", UUID.fromString(projectId))
                .getResultList();
    }

    /**
     * 특정 프로젝트에 참여한 파트너 리스트 조회
     *
     * @param projectId 프로젝트 식별 번호
     * @return SelectProjectEntity 리스트
     */
    public List<SelectProjectEntity> findPartnersByProjectId(String projectId) {
        return entityManager.createQuery(
                        "SELECT sp FROM SelectProjectEntity sp WHERE sp.project.projectId = :projectId", SelectProjectEntity.class)
                .setParameter("projectId", UUID.fromString(projectId))
                .getResultList();
    }

    public Optional<ProjectEntity> findByUUIDToString(String uuidString) {
        try {
            UUID uuid = UUID.fromString(uuidString);
            String jpql = "SELECT p FROM ProjectEntity p WHERE p.projectId = :uuid";
            TypedQuery<ProjectEntity> query = entityManager.createQuery(jpql, ProjectEntity.class);
            query.setParameter("uuid", uuid);
            List<ProjectEntity> results = query.getResultList();
            if (results.isEmpty()) return Optional.empty();
            return Optional.of(results.get(0));
        } catch (IllegalArgumentException e) {
            // 잘못된 UUID 형식일 때
            return Optional.empty();
        }
    }

    public List<CompletedProjectDTO> findCompletedProjectsWithContract(String clientMemberId) {

        String jpql = """
            SELECT new com.withus.project.dto.projects.CompletedProjectDTO(
                CAST(p.projectId as string),
                p.projectName,
                p.amount,
                CAST(c.contractId as string),
                c.amount,
                c.status,
                p.progressStatus
            )
            FROM ContractEntity c
            JOIN c.project p
            WHERE p.isCompleted = true
              AND p.client.member.id = :clientMemberId
        """;

        return getEntityManager()
                .createQuery(jpql, CompletedProjectDTO.class)
                .setParameter("clientMemberId", clientMemberId)
                .getResultList();
    }








}

//수정완
