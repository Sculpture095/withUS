package com.withus.project.repository.members;

import com.withus.project.domain.members.CertificateEntity;
import com.withus.project.repository.AbstractRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(transactionManager = "transactionManager")
@RequiredArgsConstructor
public class CertificateRepositoryImpl {

    @PersistenceContext
    private EntityManager entityManager;
    private final MemberRepositoryImpl memberRepository;



    public List<CertificateEntity> findCertificatesByMemberId(String memberId) {
        Integer partnerIdx = memberRepository.findPartnerIdxByMemberId(memberId); // ✅ 기존 메서드 활용
        if (partnerIdx == null) {
            return List.of(); // 해당 멤버가 파트너가 아닌 경우 빈 리스트 반환
        }

        return entityManager.createQuery(
                        "SELECT c FROM CertificateEntity c WHERE c.partner.partnerIdx = :partnerIdx", CertificateEntity.class)
                .setParameter("partnerIdx", partnerIdx)
                .getResultList();
    }

    /**
     * 자격증 추가
     */
    public void saveCertificate(CertificateEntity certificate) {
        entityManager.persist(certificate);
    }

    /**
     * 자격증 삭제
     */
    public void deleteCertificate(String certificateId) {
        CertificateEntity certificate = entityManager.find(CertificateEntity.class, certificateId);
        if (certificate != null) {
            entityManager.remove(certificate);
        }
    }



}
