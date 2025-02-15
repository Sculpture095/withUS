package com.withus.project.repository.projects;

import com.withus.project.domain.members.PartnerEntity;
import com.withus.project.domain.members.SelectSkillEntity;
import com.withus.project.domain.members.SkillType;
import com.withus.project.domain.projects.ProjectEntity;
import com.withus.project.repository.members.PartnerRepositoryImpl;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SelectSkillRepositoryImpl {

    @PersistenceContext
    private EntityManager entityManager;

    public void save(SelectSkillEntity selectSkill) {
        entityManager.persist(selectSkill);
    }

    public void deleteByProjectAndSkillType(ProjectEntity project, SkillType skillType) {
        String jpql = "DELETE FROM SelectSkillEntity s WHERE s.project = :project AND s.skillType = :skillType";
        entityManager.createQuery(jpql)
                .setParameter("project", project)
                .setParameter("skillType", skillType)
                .executeUpdate();
    }

    public void addSkillToPartner(String memberId, SkillType skillType, Integer careerDuration, PartnerRepositoryImpl partnerRepository) {
        Integer partnerIdx = partnerRepository.findPartnerIdxByMemberId(memberId);

        if (partnerIdx == null) {
            throw new IllegalArgumentException("해당 회원은 파트너가 아닙니다.");
        }

        PartnerEntity partner = entityManager.find(PartnerEntity.class, partnerIdx);

        if (partner == null) {
            throw new IllegalArgumentException("해당 Partner를 찾을 수 없습니다.");
        }

        SelectSkillEntity selectSkill = new SelectSkillEntity();
        selectSkill.setPartner(partner);
        selectSkill.setSkillType(skillType);
        selectSkill.setCareerDuration(careerDuration);

        entityManager.persist(selectSkill);
    }

    public List<SelectSkillEntity> findSkillsByPartnerIdx(Integer partnerIdx) {
        return entityManager.createQuery(
                        "SELECT s FROM SelectSkillEntity s WHERE s.partner.partnerIdx = :partnerIdx", SelectSkillEntity.class)
                .setParameter("partnerIdx", partnerIdx)
                .getResultList();
    }

}
