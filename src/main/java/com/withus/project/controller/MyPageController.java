package com.withus.project.controller;

import com.withus.project.domain.dto.members.MemberDTO;
import com.withus.project.domain.dto.members.MyPageDTO;
import com.withus.project.service.member.MyPageService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class MyPageController {

    private final MyPageService myPageService;

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
    public String partnerMyPage(HttpSession session, Model model){
        if(session.getAttribute("member")==null){
            return "redirect:/login";
        }
        model.addAttribute("member",session.getAttribute("member"));
        return "partner_myPage/p_myPage";
    }




}
