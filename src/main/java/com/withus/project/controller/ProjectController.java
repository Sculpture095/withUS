package com.withus.project.controller;

import com.withus.project.domain.dto.members.MemberDTO;
import com.withus.project.domain.dto.projects.ProjectDTO;
import com.withus.project.domain.members.ClientEntity;
import com.withus.project.domain.members.PcaType;
import com.withus.project.domain.projects.ProjectStatus;
import com.withus.project.service.ProjectService;
import com.withus.project.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller // ✅ 뷰 반환을 위한 컨트롤러
@RequiredArgsConstructor
@RequestMapping("/findProject") // ✅ "/findProject" 경로에서 HTML 반환
public class ProjectController {

    private final ProjectService projectService;



    @GetMapping
    public String getProjects(@RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "10") int size,
                              Model model) {
        var pageResponse = projectService.getPagedProjects(page, size);

        model.addAttribute("projects", pageResponse.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", pageResponse.getTotalPages());

        return "findProject";
    }

    @GetMapping("/filter")
    public String filterProjects(@RequestParam(required = false) ProjectStatus status,
                                 @RequestParam(required = false) String sortType,
                                 Model model) {
        List<ProjectDTO> projects;

        if (status != null) {
            projects = projectService.getProjectsByStatus(status);
        } else {
            projects = projectService.getAllProjects();
        }

        if (sortType != null) {
            switch (sortType) {
                case "amount_asc":
                    projects = projects.stream()
                            .sorted(Comparator.comparing(ProjectDTO::getAmount))
                            .collect(Collectors.toList());
                    break;
                case "amount_desc":
                    projects = projects.stream()
                            .sorted(Comparator.comparing(ProjectDTO::getAmount).reversed())
                            .collect(Collectors.toList());
                    break;
                case "teamSize":
                    projects = projects.stream()
                            .sorted(Comparator.comparing(ProjectDTO::getTeamSize))
                            .collect(Collectors.toList());
                    break;
                case "startDate_asc":
                    projects = projects.stream()
                            .sorted(Comparator.comparing(ProjectDTO::getStartDate))
                            .collect(Collectors.toList());
                    break;
                case "startDate_desc":
                    projects = projects.stream()
                            .sorted(Comparator.comparing(ProjectDTO::getStartDate).reversed())
                            .collect(Collectors.toList());
                    break;
            }
        }

        int totalPages = (projects.size() + 9) / 10; // 10개 단위로 페이지 계산
        model.addAttribute("projects", projects);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("currentPage", 0);

        return "findProject";
    }

    @GetMapping("/search")
    public String searchProjects(@RequestParam(required = false) String keyword,
                                 @RequestParam(required = false) ProjectStatus status,
                                 @RequestParam(required = false) Double minAmount,
                                 @RequestParam(required = false) Double maxAmount,
                                 Model model) {
        List<ProjectDTO> projects = projectService.searchProjects(keyword, status, minAmount, maxAmount);

        model.addAttribute("projects", projects);
        model.addAttribute("totalPages", (projects.size() + 9) / 10);
        model.addAttribute("currentPage", 0);

        return "findProject";
    }

    @GetMapping("/detail/{projectId}")
    public String getProjectDetail(@PathVariable UUID projectId, Model model) {
        ProjectDTO project = projectService.getProjectById(projectId);

        if (project == null) {
            return "redirect:/findProject"; // 프로젝트가 존재하지 않으면 목록으로 리다이렉트
        }

        model.addAttribute("project", project);
        return "findProject_detail";
    }

}
