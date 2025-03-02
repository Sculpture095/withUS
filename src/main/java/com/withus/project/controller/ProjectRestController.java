package com.withus.project.controller;

import com.withus.project.dto.ApplicantSelectionRequestDTO;
import com.withus.project.dto.members.MemberDTO;
import com.withus.project.dto.projects.ProjectDTO;
import com.withus.project.service.ProjectService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectRestController {
    private final ProjectService projectService;

    //프로젝트 등록 API
    @PostMapping
    public ResponseEntity<ProjectDTO> registerProject(
            @RequestBody ProjectDTO projectDTO,
            @RequestParam(required = false) List<String> selectedSkills,
            HttpSession session) {
        MemberDTO member = (MemberDTO) session.getAttribute("member");
        if (member == null) {
            throw new IllegalArgumentException("로그인이 필요합니다.");
        }

        // 디버깅: 선택된 기술 목록 출력
        if (selectedSkills != null && !selectedSkills.isEmpty()) {
            System.out.println(">>> 선택된 기술 스택: " + selectedSkills);
            for (String skillName : selectedSkills) {
                System.out.println(">>> skillName = " + skillName);
            }
        } else {
            System.out.println(">>> selectedSkills가 null이거나 비어 있습니다.");
        }

        // 세션 사용자의 ID 주입
        projectDTO.setMemberId(member.getId());

        // createProjectWithSkills 메서드를 호출하여 프로젝트와 스킬들을 저장
        ProjectDTO createdProject = projectService.createProjectWithSkills(projectDTO, selectedSkills);
        return ResponseEntity.ok(createdProject);
    }

    @PostMapping("/applicants/select")
    public ResponseEntity<?> selectApplicants(
            @RequestBody ApplicantSelectionRequestDTO request,
            HttpSession session
    ) {
        // 1) 로그인/권한 체크
        MemberDTO member = (MemberDTO) session.getAttribute("member");
        if (member == null || !"CLIENT".equals(member.getPcaType())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message","권한 없음"));
        }

        // 2) Service 로직 호출
        try {
            projectService.chooseApplicantsAndSetMeeting(
                    member.getId(),
                    request.getProjectId(),
                    request.getApplicantIds(),
                    request.getMeetingDate(),
                    request.getMeetingTime()
            );
            return ResponseEntity.ok(Map.of("message", "지원자 선택 & 미팅 일정 저장 완료"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/end")
    public ResponseEntity<?> endProject(
            @RequestParam String projectId,
            HttpSession session
    ) {
        MemberDTO member = (MemberDTO) session.getAttribute("member");
        if(member == null || !"CLIENT".equals(member.getPcaType())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message","권한이 없습니다."));
        }

        try {
            projectService.endProject(member.getId(), projectId);
            return ResponseEntity.ok(Map.of("message","프로젝트가 종료되었습니다."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

}
