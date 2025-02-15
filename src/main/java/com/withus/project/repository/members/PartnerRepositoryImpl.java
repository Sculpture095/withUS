package com.withus.project.repository.members;

import com.withus.project.domain.members.CertificateEntity;
import com.withus.project.domain.members.PartnerEntity;
import com.withus.project.repository.AbstractRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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

}
