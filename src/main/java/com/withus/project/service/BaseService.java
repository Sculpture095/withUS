package com.withus.project.service;

import java.util.List;

public interface BaseService<D> {
    D create(D dto); //생성
    D update(String id, D dto); //업데이트 (외부에는 String id 사용)
    D findById(String id); //단일 조회 (외부에는 String  id 사용)
    List<D> findAll(); //전체 조회
    void delete(String id); //삭제 (외부에는 String id 사용)
}
