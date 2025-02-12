package com.withus.project.service;

import com.withus.project.mapper.BaseMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class BaseServiceImpl<D,E> implements BaseService<D> {

    @PersistenceContext
    protected EntityManager entityManager;

    protected final Class<E> entityClass;
    protected final BaseMapper<D,E> mapper;

    public BaseServiceImpl(Class<E> entityClass, BaseMapper<D, E> mapper) {
        this.entityClass = entityClass;
        this.mapper = mapper;
    }

    /*
     * Entity의 idx를 String ID로 변환하는 추상 메서드
     * 각 서비스에서 구현 필요
     */
    protected abstract Optional<Integer> convertIdToIdx(String id);

    @Override
    @Transactional
    public D create(D dto) {
        E entity = mapper.toEntity(dto);
        entityManager.persist(entity);
        return mapper.toDTO(entity);
    }

    @Override
    @Transactional
    public D update(String id, D dto) {
        Integer idx = convertIdToIdx(id)
                .orElseThrow(() -> new IllegalArgumentException("ID를 찾을수 없습니다.: "+id));

        E existingEntity = entityManager.find(entityClass,idx);
        if(existingEntity == null){
            throw new IllegalArgumentException("Entity를 찾을수 없습니다.: "+id);
        }

        E updatedEntity = mapper.updateEntity(dto, existingEntity);
        return mapper.toDTO(updatedEntity);
    }

    @Override
    public D findById(String id) {
        Integer idx = convertIdToIdx(id)
                .orElseThrow(() -> new IllegalArgumentException("ID를 찾을수 없습니다.: "+id));

        E entity = entityManager.find(entityClass, idx);
        if(entity == null){
            throw new IllegalArgumentException("Entity를 찾을수 없습니다: "+id);
        }
        return mapper.toDTO(entity);
    }

    @Override
    public List<D> findAll() {
       return entityManager.createQuery("SELECT e FROM "+entityClass.getSimpleName()+"e", entityClass)
               .getResultList().stream()
               .map(mapper::toDTO)
               .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void delete(String id) {
        Integer idx = convertIdToIdx(id)
                .orElseThrow(() -> new IllegalArgumentException("ID를 찾을수 없습니다: "+id));

        E entity = entityManager.find(entityClass, idx);
        if(entity != null){
            entityManager.remove(entity);
        }
    }
}
