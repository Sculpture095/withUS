package com.withus.project.repository.members;

import com.withus.project.domain.members.CertificateEntity;
import com.withus.project.domain.members.PartnerEntity;
import com.withus.project.repository.AbstractRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional(transactionManager = "transactionManager")
public class PartnerRepositoryImpl extends AbstractRepository<PartnerEntity> {

    public PartnerRepositoryImpl(EntityManager entityManager) {
        super(entityManager);
    }

    @Override
    protected Class<PartnerEntity> getEntityClass() {
        return PartnerEntity.class;
    }

    @Override
    protected EntityManager getEntityManager() {
        return super.getEntityManager();
    }


    // 자격증 추가
    public CertificateEntity addCertificate(CertificateEntity certificate, String id) {
        PartnerEntity partner = findPartnerById(id);
        if (partner == null) {
            throw new EntityNotFoundException("회원 ID에 해당하는 Partner를 찾을 수 없습니다: " + id);
        }
        certificate.setPartner(partner);
        entityManager.persist(certificate);
        return certificate;
    }

    // 자격증 수정
    public CertificateEntity updateCertificate(String certificateId, CertificateEntity updatedCertificate, String id) {
        CertificateEntity existingCertificate = findCertificateById(certificateId, id);
        if (existingCertificate == null) {
            throw new EntityNotFoundException("자격증 식별 번호에 해당하는 데이터를 찾을 수 없습니다: " + certificateId);
        }

        updatedCertificate.setCertificateId(existingCertificate.getCertificateId());
        updatedCertificate.setPartner(existingCertificate.getPartner()); // 기존 Partner 유지
        return entityManager.merge(updatedCertificate);
    }

    // 자격증 삭제
    public void deleteCertificate(String certificateId, String id) {
        CertificateEntity certificate = findCertificateById(certificateId, id);
        if (certificate == null) {
            throw new EntityNotFoundException("자격증 식별 번호에 해당하는 데이터를 찾을 수 없습니다: " + certificateId);
        }
        entityManager.remove(certificate);
    }

    // ID를 기반으로 PartnerEntity 조회
    public PartnerEntity findPartnerById(String id) {
        try {
            return entityManager.createQuery(
                            "SELECT p FROM PartnerEntity p WHERE p.member.id = :id", PartnerEntity.class)
                    .setParameter("id", id)
                    .getSingleResult();
        } catch (Exception e) {
            throw new EntityNotFoundException("회원 ID에 해당하는 Partner를 찾을 수 없습니다: " + id);
        }
    }

    // ID와 certificateIdx로 자격증 단건 조회
    public CertificateEntity findCertificateById(String certificateId, String id) {
        try {
            return entityManager.createQuery(
                            "SELECT c FROM CertificateEntity c WHERE c.certificateId = :certificateId " +
                                    "AND c.partner.member.id = :id", CertificateEntity.class)
                    .setParameter("certificateId", certificateId)
                    .setParameter("id", id)
                    .getSingleResult();
        } catch (Exception e) {
            throw new EntityNotFoundException("자격증 식별 번호에 해당하는 데이터를 찾을 수 없습니다: " + certificateId);
        }
    }

    // ID를 기반으로 자격증 리스트 조회
    public List<CertificateEntity> findCertificatesById(String id) {
        return entityManager.createQuery(
                        "SELECT c FROM CertificateEntity c WHERE c.partner.member.id = :id", CertificateEntity.class)
                .setParameter("id", id)
                .getResultList();
    }

    public Integer findPartnerIdxByMemberId(String memberId) {
        return entityManager.createQuery(
                        "SELECT p.partnerIdx FROM PartnerEntity p WHERE p.member.id = :memberId", Integer.class)
                .setParameter("memberId", memberId)
                .getSingleResult();
    }

    public Optional<PartnerEntity> findByMemberId(String memberId) {
        return entityManager.createQuery(
                        "SELECT p FROM PartnerEntity p WHERE p.member.memberIdx = " +
                                "(SELECT m.memberIdx FROM MemberEntity m WHERE m.id = :memberId)", PartnerEntity.class)
                .setParameter("memberId", memberId)
                .getResultStream()
                .findFirst();
    }

    //페이징 처리된 데이터 조회
    public List<PartnerEntity> findAllPartners(int offset, int limit){
        String jpql = "SELECT p FROM PartnerEntity p ORDER BY p.partnerIdx";
        TypedQuery<PartnerEntity> query = entityManager.createQuery(jpql, PartnerEntity.class);
        query.setFirstResult(offset);
        query.setMaxResults(limit);
        return query.getResultList();
    }

    // 전체 데이터 건수 조회
    public long countAllPartners() {
        String jpql = "SELECT COUNT(p) FROM PartnerEntity p";
        return entityManager.createQuery(jpql, Long.class)
                .getSingleResult();
    }

    // 검색어가 있을 경우: partner.member.id 및 소개글(myPage.introduce)에서 검색
    public List<PartnerEntity> findAllPartners(String keyword, int offset, int limit){
        String jpql = "SELECT p FROM PartnerEntity p " +
                "WHERE p.member.id LIKE :keyword OR p.member.myPage.introduce LIKE :keyword " +
                "ORDER BY SIZE(p.portfolios) DESC, p.member.rankType DESC";
        TypedQuery<PartnerEntity> query = entityManager.createQuery(jpql, PartnerEntity.class);
        query.setParameter("keyword", "%" + keyword + "%");
        query.setFirstResult(offset);
        query.setMaxResults(limit);
        return query.getResultList();
    }

    public long countAllPartners(String keyword) {
        String jpql = "SELECT COUNT(p) FROM PartnerEntity p " +
                "WHERE p.member.id LIKE :keyword OR p.member.myPage.introduce LIKE :keyword";
        return entityManager.createQuery(jpql, Long.class)
                .setParameter("keyword", "%" + keyword + "%")
                .getSingleResult();

    }

    // 포트폴리오 많은순 정렬 (검색 미적용)
    public List<PartnerEntity> findAllPartnersOrderByPortfolioCount(int offset, int limit) {
        String jpql = "SELECT p FROM PartnerEntity p ORDER BY SIZE(p.portfolios) DESC";
        TypedQuery<PartnerEntity> query = entityManager.createQuery(jpql, PartnerEntity.class);
        query.setFirstResult(offset);
        query.setMaxResults(limit);
        return query.getResultList();
    }

    // 등급 높은순 정렬 (검색 미적용)
    public List<PartnerEntity> findAllPartnersOrderByRank(int offset, int limit) {
        String jpql = "SELECT p FROM PartnerEntity p ORDER BY p.member.rankType DESC";
        TypedQuery<PartnerEntity> query = entityManager.createQuery(jpql, PartnerEntity.class);
        query.setFirstResult(offset);
        query.setMaxResults(limit);
        return query.getResultList();
    }

    // 검색 기능 + 포트폴리오 정렬
    public List<PartnerEntity> findAllPartnersByKeywordOrderByPortfolioCount(String keyword, int offset, int limit) {
        String jpql = "SELECT p FROM PartnerEntity p " +
                "WHERE p.member.id LIKE :keyword OR p.member.myPage.introduce LIKE :keyword " +
                "ORDER BY SIZE(p.portfolios) DESC";
        TypedQuery<PartnerEntity> query = entityManager.createQuery(jpql, PartnerEntity.class);
        query.setParameter("keyword", "%" + keyword + "%");
        query.setFirstResult(offset);
        query.setMaxResults(limit);
        return query.getResultList();
    }

    // 검색 기능 + 등급 정렬
    public List<PartnerEntity> findAllPartnersByKeywordOrderByRank(String keyword, int offset, int limit) {
        String jpql = "SELECT p FROM PartnerEntity p " +
                "WHERE p.member.id LIKE :keyword OR p.member.myPage.introduce LIKE :keyword " +
                "ORDER BY p.member.rankType DESC";
        TypedQuery<PartnerEntity> query = entityManager.createQuery(jpql, PartnerEntity.class);
        query.setParameter("keyword", "%" + keyword + "%");
        query.setFirstResult(offset);
        query.setMaxResults(limit);
        return query.getResultList();
    }

    public List<PartnerEntity> findPartnersByFilter(List<String> ranks,
                                                    List<String> types,
                                                    String sort,
                                                    int offset,
                                                    int limit) {
        // 1) JPQL 동적 생성
        StringBuilder jpql = new StringBuilder("SELECT p FROM PartnerEntity p WHERE 1=1");

        // 등급 필터
        if (ranks != null && !ranks.isEmpty()) {
            jpql.append(" AND p.member.rankType IN :ranks");
        }
        // 파트너 형태 필터
        if (types != null && !types.isEmpty()) {
            jpql.append(" AND p.member.userType IN :types");
        }

        // 정렬
        switch (sort) {
            case "portfolio":
                // 포트폴리오 많은 순
                jpql.append(" ORDER BY SIZE(p.portfolios) DESC");
                break;
            case "score":
                // 평점 높은 순(예: p.score 컬럼이 있다고 가정)
                jpql.append(" ORDER BY p.score DESC");
                break;
            default:
                // 기본 정렬(파트너 등록순 등)
                jpql.append(" ORDER BY p.partnerIdx DESC");
                break;
        }

        TypedQuery<PartnerEntity> query = entityManager.createQuery(jpql.toString(), PartnerEntity.class);

        // 파라미터 세팅
        if (ranks != null && !ranks.isEmpty()) {
            query.setParameter("ranks", ranks);
        }
        if (types != null && !types.isEmpty()) {
            query.setParameter("types", types);
        }

        query.setFirstResult(offset);
        query.setMaxResults(limit);

        return query.getResultList();
    }

    public long countPartnersByFilter(List<String> ranks, List<String> types) {
        StringBuilder jpql = new StringBuilder("SELECT COUNT(p) FROM PartnerEntity p WHERE 1=1");

        if (ranks != null && !ranks.isEmpty()) {
            jpql.append(" AND p.member.rankType IN :ranks");
        }
        if (types != null && !types.isEmpty()) {
            jpql.append(" AND p.member.userType IN :types");
        }

        TypedQuery<Long> query = entityManager.createQuery(jpql.toString(), Long.class);

        if (ranks != null && !ranks.isEmpty()) {
            query.setParameter("ranks", ranks);
        }
        if (types != null && !types.isEmpty()) {
            query.setParameter("types", types);
        }

        return query.getSingleResult();
    }



}
