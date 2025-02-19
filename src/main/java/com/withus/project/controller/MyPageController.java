package com.withus.project.controller;

import com.withus.project.domain.dto.members.MemberDTO;
import com.withus.project.domain.dto.members.MyPageDTO;
import com.withus.project.domain.dto.projects.ProjectDTO;
import com.withus.project.domain.members.HistoryEntity;
import com.withus.project.repository.members.HistoryRepositoryImpl;
import com.withus.project.service.ProjectService;
import com.withus.project.service.member.MyPageService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class MyPageController {

    private final MyPageService myPageService;
    private final ProjectService projectService;
    private final HistoryRepositoryImpl historyRepository;

    @GetMapping("/myPage")
    public String myPage(HttpSession session, RedirectAttributes redirectAttributes){
        String pcaType = (String) session.getAttribute("pcaType");
        System.out.println("pcaType in session: " + pcaType); // 로그 추가

        if (pcaType == null){
            redirectAttributes.addFlashAttribute("alertMessage","로그인이 필요합니다.");
            return "redirect:/login";
        }

        if("PARTNER".equals(pcaType)){
            return "redirect:/p_myPage";
        } else if ("CLIENT".equals(pcaType)) {
            return "redirect:/c_myPage";
        }else {
            redirectAttributes.addFlashAttribute("alertMessage","잘못된 접근입니다.");
            return "redirect:/";
        }
    }

    @GetMapping("/c_myPage")
    public String clientMyPage(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        System.out.println("🔍 [MyPageController] /c_myPage 요청 들어옴");

        MemberDTO member = (MemberDTO) session.getAttribute("member");
        System.out.println("🛠 [DEBUG] 세션에서 가져온 member: " + member);

        if (member == null) {
            System.out.println("⚠ [ERROR] 세션에 member 없음, 로그인 페이지로 이동");
            redirectAttributes.addFlashAttribute("alertMessage", "로그인이 필요합니다.");
            return "redirect:/login";
        }

        try {
            MyPageDTO myPage = myPageService.getMyPageByUserId(member.getId());
            System.out.println("✅ [MyPageController] 마이페이지 데이터 조회 완료: " + myPage);
            model.addAttribute("member", member);
            model.addAttribute("myPage", myPage);
        } catch (EntityNotFoundException e) {
            System.out.println("❌ [MyPageController] 마이페이지 정보를 찾을 수 없음");
            redirectAttributes.addFlashAttribute("alertMessage", "마이페이지 정보를 찾을 수 없습니다.");
            return "redirect:/";
        }

        return "client_myPage/c_myPage";
    }



    @GetMapping("/p_myPage")
    public String partnerMyPage(HttpSession session, Model model, RedirectAttributes redirectAttributes){
        System.out.println("🔍 [MyPageController] /p_myPage 요청 들어옴");

        MemberDTO member = (MemberDTO) session.getAttribute("member");
        System.out.println("🛠 [DEBUG] 세션에서 가져온 member: " + member);

        if (member == null) {
            System.out.println("⚠ [ERROR] 세션에 member 없음, 로그인 페이지로 이동");
            redirectAttributes.addFlashAttribute("alertMessage", "로그인이 필요합니다.");
            return "redirect:/login";
        }
        try {
            MyPageDTO myPage = myPageService.getMyPageByUserId(member.getId());
            System.out.println("✅ [MyPageController] 마이페이지 데이터 조회 완료: " + myPage);
            model.addAttribute("member", member);
            model.addAttribute("myPage", myPage);
        } catch (EntityNotFoundException e) {
            System.out.println("❌ [MyPageController] 마이페이지 정보를 찾을 수 없음");
            redirectAttributes.addFlashAttribute("alertMessage", "마이페이지 정보를 찾을 수 없습니다.");
            return "redirect:/";
        }

        return "partner_myPage/p_myPage";
    }



    @PostMapping("/api/updateMyPage")
    @ResponseBody
    public Map<String, Boolean> updateMyPage(HttpSession session, @RequestBody Map<String, Object> requestData) {
        MemberDTO member = (MemberDTO) session.getAttribute("member");
        if (member == null) {
            throw new EntityNotFoundException("세션에 회원 정보가 없습니다.");
        }

        MyPageDTO myPage = myPageService.getMyPageByUserId(member.getId());
        UUID myPageId = UUID.fromString(myPage.getMyPageId());

        myPageService.updateCommonFields(myPageId, requestData);

        return Map.of("success", true);
    }


//    @GetMapping("/c_project")
//    public String clientProjectPage(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
//        MemberDTO member = (MemberDTO) session.getAttribute("member");
//
//        if (member == null) {
//            redirectAttributes.addFlashAttribute("alertMessage", "로그인이 필요합니다.");
//            return "redirect:/login";
//        }
//
//        // ✅ 로그 추가: 클라이언트 ID 확인
//        System.out.println("🔍 [MyPageController] 클라이언트 ID: " + member.getId());
//
//        // ✅ 클라이언트가 등록한 프로젝트 목록 조회
//        List<ProjectDTO> projects = projectService.getClientProjects(member.getId());
//
//        // ✅ 로그 추가: 조회된 프로젝트 확인
//        System.out.println("📌 [MyPageController] 조회된 프로젝트 개수: " + projects.size());
//        for (ProjectDTO project : projects) {
//            System.out.println("📌 프로젝트: " + project.getProjectName());
//        }
//
//        // 모델에 데이터 추가
//        model.addAttribute("projects", projects);
//        model.addAttribute("member", member);
//
//        return "client_myPage/c_project";
//    }
//
//
//    @GetMapping("/p_project")
//    public String partnerProjectPage(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
//        MemberDTO member = (MemberDTO) session.getAttribute("member");
//
//        if (member == null) {
//            redirectAttributes.addFlashAttribute("alertMessage", "로그인이 필요합니다.");
//            return "redirect:/login";
//        }
//
//        // ✅ 로그 추가: 클라이언트 ID 확인
//        System.out.println("🔍 [MyPageController] 클라이언트 ID: " + member.getId());
//
//        // ✅ 클라이언트가 등록한 프로젝트 목록 조회
//        List<ProjectDTO> projects = projectService.getClientProjects(member.getId());
//
//        // ✅ 로그 추가: 조회된 프로젝트 확인
//        System.out.println("📌 [MyPageController] 조회된 프로젝트 개수: " + projects.size());
//        for (ProjectDTO project : projects) {
//            System.out.println("📌 프로젝트: " + project.getProjectName());
//        }
//
//        // 모델에 데이터 추가
//        model.addAttribute("projects", projects);
//        model.addAttribute("member", member);
//
//        return "client_myPage/c_project";
//    }







}
