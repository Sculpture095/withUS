package com.withus.project.controller;

import com.withus.project.domain.dto.members.MemberDTO;
import com.withus.project.domain.dto.projects.ProjectDTO;
import com.withus.project.domain.members.SkillType;
import com.withus.project.service.ProjectService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

}
