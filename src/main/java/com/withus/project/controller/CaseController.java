package com.withus.project.controller;


import com.withus.project.dto.projects.CaseDTO;
import com.withus.project.service.CaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cases")
@RequiredArgsConstructor
public class CaseController {

    private final CaseService caseService;

    @PostMapping
    public ResponseEntity<?> createCase(@RequestBody CaseDTO dto) {
        try {
            CaseDTO saved = caseService.createCase(dto);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("고객 사례 등록 실패: " + e.getMessage());
        }
    }

    @GetMapping("/{caseId}")
    public ResponseEntity<?> getCaseByCaseId(@PathVariable String caseId) {
        CaseDTO dto = caseService.getCase(caseId);
        if (dto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dto);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleAnyException(Exception e) {
        e.printStackTrace();
        return ResponseEntity.badRequest().body("오류: " + e.getMessage());
    }
}
