package com.withus.project.controller;

import com.withus.project.domain.dto.PageResponse;
import com.withus.project.domain.dto.members.MemberDTO;
import com.withus.project.domain.dto.projects.ProjectDTO;
import com.withus.project.domain.members.ClientEntity;
import com.withus.project.domain.members.PcaType;
import com.withus.project.domain.members.SkillType;
import com.withus.project.domain.projects.ProjectEntity;
import com.withus.project.domain.projects.ProjectStatus;
import com.withus.project.service.ProjectService;
import com.withus.project.service.member.MemberService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    public String getProjects(Model model,
                              @RequestParam(value = "page", defaultValue = "0") int page) {
        int size = 5;
        PageResponse<ProjectDTO> projects = projectService.getAllProjectsDTO(page, size); // DTO 버전

        int blockSize = 10;
        int startPage = (page / blockSize) * blockSize;
        int endPage = Math.min(startPage + blockSize - 1, projects.getTotalPages() - 1);

        model.addAttribute("projects", projects);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        return "findProject";
    }

    @GetMapping("/client")
    public String getClientProjects(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        MemberDTO member = (MemberDTO) session.getAttribute("member");

        if (member == null) {
            redirectAttributes.addFlashAttribute("alertMessage", "로그인이 필요합니다.");
            return "redirect:/login";
        }

        List<ProjectDTO> projects = projectService.getClientProjects(member.getId());
        model.addAttribute("projects", projects);
        model.addAttribute("member", member);

        return "client_myPage/c_project";
    }

    @GetMapping("/partner/applied")
    public String getAppliedProjects(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        MemberDTO member = (MemberDTO) session.getAttribute("member");

        if (member == null) {
            redirectAttributes.addFlashAttribute("alertMessage", "로그인이 필요합니다.");
            return "redirect:/login";
        }

        List<ProjectDTO> appliedProjects = projectService.getAppliedProjectsByPartner(member.getId());
        model.addAttribute("appliedProjects", appliedProjects);
        model.addAttribute("member", member);


        return "partner_myPage/p_project";
    }


    @GetMapping("/filter")
    public String filterProjects(@RequestParam(required = false) ProjectStatus status,
                                 @RequestParam(required = false) String sortType,
                                 @RequestParam(value = "page", defaultValue = "0") int page,
                                 Model model) {
        List<ProjectDTO> projectList;

        if (status != null) {
            projectList = projectService.getProjectsByStatus(status);
        } else {
            projectList = projectService.getAllProjects();
        }

        if (sortType != null) {
            switch (sortType) {
                case "amount_asc":
                    projectList = projectList.stream()
                            .sorted(Comparator.comparing(ProjectDTO::getAmount))
                            .collect(Collectors.toList());
                    break;
                case "amount_desc":
                    projectList = projectList.stream()
                            .sorted(Comparator.comparing(ProjectDTO::getAmount).reversed())
                            .collect(Collectors.toList());
                    break;
                case "teamSize":
                    projectList = projectList.stream()
                            .sorted(Comparator.comparing(ProjectDTO::getTeamSize))
                            .collect(Collectors.toList());
                    break;
                case "startDate_asc":
                    projectList = projectList.stream()
                            .sorted(Comparator.comparing(ProjectDTO::getStartDate))
                            .collect(Collectors.toList());
                    break;
                case "startDate_desc":
                    projectList = projectList.stream()
                            .sorted(Comparator.comparing(ProjectDTO::getStartDate).reversed())
                            .collect(Collectors.toList());
                    break;
            }
        }

        int size = 5;
        int totalElements = projectList.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);

        int offset = page * size;
        int end = Math.min(offset + size, totalElements);
        List<ProjectDTO> content = projectList.subList(offset, end);

        // 4) PageResponse로 감싸기
        PageResponse<ProjectDTO> projects = PageResponse.<ProjectDTO>builder()
                .content(content)
                .page(page)
                .size(size)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .build();

        // 5) 블록 계산
        int blockSize = 10;
        int startPage = (page / blockSize) * blockSize;
        int endPage = Math.min(startPage + blockSize - 1, totalPages - 1);

        // 6) 모델에 담기
        model.addAttribute("projects", projects);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        return "findProject";
    }


    @GetMapping("/search")
    public String searchProjects(@RequestParam(required = false) String keyword,
                                 @RequestParam(required = false) ProjectStatus status,
                                 @RequestParam(required = false) Double minAmount,
                                 @RequestParam(required = false) Double maxAmount,
                                 @RequestParam(value = "page", defaultValue = "0") int page,
                                 Model model) {
        List<ProjectDTO> projectList = projectService.searchProjects(keyword, status, minAmount, maxAmount);

        // 2) 페이징 처리
        int size = 5;
        int totalElements = projectList.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);

        int offset = page * size;
        int end = Math.min(offset + size, totalElements);
        List<ProjectDTO> content = projectList.subList(offset, end);

        // 3) PageResponse 생성
        PageResponse<ProjectDTO> projects = PageResponse.<ProjectDTO>builder()
                .content(content)
                .page(page)
                .size(size)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .build();

        // 4) 블록 계산
        int blockSize = 10;
        int startPage = (page / blockSize) * blockSize;
        int endPage = Math.min(startPage + blockSize - 1, totalPages - 1);

        // 5) 모델에 담기
        model.addAttribute("projects", projects);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

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

    @GetMapping("/registerProject")
    public String showProjectRegistrationPage(HttpSession session, Model model, RedirectAttributes redirectAttributes){
        MemberDTO member = (MemberDTO) session.getAttribute( "member");
        if(member == null){
            redirectAttributes.addFlashAttribute("alertMessage","로그인이 필요합니다.");
            return "redirect:/login";
        }
        if(!member.getPcaType().equals("CLIENT")){
            redirectAttributes.addFlashAttribute("alertMessage", "프로젝트 등록 권한이 없습니다.");
            return "redirect:/";
        }
        //기술스택 선택을 위한 SkillType 목록을 뷰에 전달
        model.addAttribute("skillTypes", SkillType.values());
        //폼 바인딩을 위한 빈 ProjectDTO 객체 생성
        model.addAttribute("project", new ProjectDTO());

        //Thymeleaf 탬플릿 registerProject.html에 반환
        return "registerProject";
    }



}
