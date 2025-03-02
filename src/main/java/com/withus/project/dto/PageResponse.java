package com.withus.project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class PageResponse<T> {
    private List<T> content;  // 현재 페이지 데이터
    private int page;         // 현재 페이지 번호
    private int size;         // 페이지 크기
    private long totalElements; // 전체 데이터 개수
    private int totalPages;   // 전체 페이지 개수

    public PageResponse(List<T>content, int page, int totalPages, long totalElements){
        this.content = content;
        this.page = page;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
    }
}
