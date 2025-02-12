package com.withus.project.repository.projects;

import com.withus.project.domain.members.SelectSkillEntity;
import com.withus.project.domain.members.SkillType;
import com.withus.project.domain.projects.ProjectEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

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
}
