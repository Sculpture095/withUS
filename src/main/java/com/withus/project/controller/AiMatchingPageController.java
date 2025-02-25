package com.withus.project.controller;

import com.withus.project.domain.dto.projects.ProjectDTO;
import com.withus.project.service.AiMatchingService;
import com.withus.project.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class AiMatchingPageController {

    private final AiMatchingController aiMatchingController;
    private  final ProjectService projectService;
    private final AiMatchingService aiMatchingService;

    @GetMapping("/ai-modal")
    public String showAiModal(Model model, Principal principal) {
        String clientId = principal.getName();
        model.addAttribute("recommendation", null);
        model.addAttribute("showModal", true);
        // 프로젝트 목록 전달
        model.addAttribute("projects", projectService.getClientProjects(clientId));
        return "client_myPage/c_project";
    }

    @PostMapping("/api/ai/recommend")
    public String recommendPartner(@RequestParam("projectId") String projectId,
                                   Model model, Principal principal) {
        String recommendation = aiMatchingService.getPartnerRecommendation(projectId);
        ProjectDTO project = projectService.getProjectById(UUID.fromString(projectId));
        String clientId = principal.getName();

        model.addAttribute("recommendation", recommendation);
        model.addAttribute("selectedProjectName", project.getProjectName());
        model.addAttribute("userNickname", clientId);
        model.addAttribute("projects", projectService.getClientProjects(clientId));
        model.addAttribute("showModal", true);

        return "client_myPage/c_project :: aiPartnerModal";
    }

}
