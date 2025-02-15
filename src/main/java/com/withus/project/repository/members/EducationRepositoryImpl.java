package com.withus.project.repository.members;

import com.withus.project.domain.members.PartnerEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(transactionManager = "transactionManager")
@RequiredArgsConstructor
public class EducationRepositoryImpl {

    @PersistenceContext
    private EntityManager entityManager;
    private final MemberRepositoryImpl memberRepository;

    // 특정 회원의 학력 정보 조회
    public PartnerEntity findEducationByMemberId(String memberId) {
        Integer partnerIdx = memberRepository.findPartnerIdxByMemberId(memberId);
        return entityManager.find(PartnerEntity.class, partnerIdx);
    }

    // 학력 추가 및 수정
    public void updateEducation(String memberId, String schoolName, String major, String graduationDate) {
        Integer partnerIdx = memberRepository.findPartnerIdxByMemberId(memberId);
        PartnerEntity partner = entityManager.find(PartnerEntity.class, partnerIdx);
        if (partner != null) {
            partner.setSchoolName(schoolName);
            partner.setMajor(major);
            partner.setGraduation(graduationDate);
            entityManager.merge(partner);
        }
    }

    // 학력 삭제
    public void deleteEducation(String memberId) {
        Integer partnerIdx = memberRepository.findPartnerIdxByMemberId(memberId);
        PartnerEntity partner = entityManager.find(PartnerEntity.class, partnerIdx);
        if (partner != null) {
            partner.setSchoolName(null);
            partner.setMajor(null);
            partner.setGraduation(null);
            entityManager.merge(partner);
        }
    }
}
