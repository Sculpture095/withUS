package com.withus.project.mapper;

public interface BaseMapper<D,E> {
    E toEntity(D dto);
    D toDTO(E entity);
    E updateEntity(D dto, E entity);
}
