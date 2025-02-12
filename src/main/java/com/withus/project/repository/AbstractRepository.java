package com.withus.project.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public abstract class AbstractRepository<T> {
    protected final EntityManager entityManager;

    protected AbstractRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    // EntityManager 반환 메서드 추가
    protected EntityManager getEntityManager() {
        return entityManager;
    }

    // save: 새로운 엔티티 저장 또는 기존 엔티티 업데이트
    public T save(T entity) {
        entityManager.persist(entity);
        return entity;
    }

    // update: 특정 id를 기반으로 엔티티 업데이트
    public void update(String id, T updatedEntity) {
        T existingEntity = findById(id).orElseThrow(() ->
                new EntityNotFoundException("Entity not found with idx: " + id));
        entityManager.merge(updatedEntity);
    }

    // 부분 업데이트 (Partial Update) 메서드 추가
    public void partialUpdate(Object id, Map<String, Object> fields) {
        T entity;
        if (id instanceof String) {
            entity = findById((String) id).orElseThrow(() ->
                    new EntityNotFoundException("Entity not found with idx: " + id));
        } else if (id instanceof UUID) {
            entity = findById((UUID) id).orElseThrow(() ->
                    new EntityNotFoundException("Entity not found with UUID: " + id));
        } else if (id instanceof Integer) { // ✅ Integer 지원 추가
            entity = findById((Integer) id).orElseThrow(() ->
                    new EntityNotFoundException("Entity not found with idx: " + id));
        } else {
            throw new IllegalArgumentException("Unsupported ID type: " + id.getClass().getSimpleName());
        }

        fields.forEach((fieldName, value) -> {
            try {
                Field field = entity.getClass().getDeclaredField(fieldName);
                field.setAccessible(true);
                field.set(entity, value);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException("Failed to update field: " + fieldName, e);
            }
        });

        entityManager.merge(entity);
    }



    // delete: 엔티티 삭제
    public void delete(T entity) {
        if (!entityManager.contains(entity)) {
            entity = entityManager.merge(entity);
        }
        entityManager.remove(entity);
    }
    public Optional<T> findById(String id) {
        return entityManager.createQuery(
                        "SELECT e FROM " + getEntityClass().getSimpleName() + " e WHERE e.id = :id",
                        getEntityClass())
                .setParameter("id", id)
                .getResultStream()
                .findFirst();
    }
    // ✅ UUID 기반 조회 (Project 등에서 사용)
    public Optional<T> findById(UUID id) {
        return entityManager.createQuery(
                        "SELECT e FROM " + getEntityClass().getSimpleName() + " e WHERE e.id = :id",
                        getEntityClass())
                .setParameter("id", id)
                .getResultStream()
                .findFirst();
    }
    public Optional<T> findById(Integer idx) {
        return entityManager.createQuery(
                        "SELECT e FROM " + getEntityClass().getSimpleName() + " e WHERE e.id = :id",
                        getEntityClass())
                .setParameter("idx", idx)
                .getResultStream()
                .findFirst();
    }



    //MemberId를 기반으로 엔티티 전체 조회
    public List<T> findAllByMemberId(String memberId) {
        return entityManager.createQuery(
                        "SELECT e FROM " + getEntityClass().getSimpleName() + " e " +
                                "WHERE e.member.memberIdx = (SELECT m.memberIdx FROM MemberEntity m WHERE m.id = :memberId)",
                        getEntityClass())
                .setParameter("memberId", memberId)
                .getResultList();
    }

    // findAll: 모든 엔티티 조회
    public List<T> findAll() {
        return entityManager.createQuery("SELECT e FROM " + getEntityClass().getSimpleName() + " e", getEntityClass())
                .getResultList();
    }

    public void deleteById(String id) {
        T entity = findById(id).orElseThrow(() -> new EntityNotFoundException("Entity not found with id: " + id));
        entityManager.remove(entity);
    }
    // 엔티티의 클래스를 반환하는 메서드 (구체화된 클래스에서 구현)
    protected abstract Class<T> getEntityClass();

}

