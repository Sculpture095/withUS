package com.withus.project.controller;

import com.withus.project.dto.members.QuestionDTO;
import com.withus.project.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/guide")
public class QuestionController {

    private final QuestionService questionService;

    @GetMapping("/c_ask")
    public String getClientAsk(){
        return "/guide/c_ask";
    }
    @GetMapping("/p_ask")
    public String getPartnerAsk(){
        return "/guide/p_ask";
    }


    @GetMapping("/new")
    public String getNewQuestion(Principal principal, Model model) {
        // 로그인 X 상태라면
        if (principal == null) {
            // alert창에 보여줄 메시지와 이동할 URL을 모델에 담아서
            model.addAttribute("msg", "로그인이 필요합니다.");
            model.addAttribute("url", "/login");
            // 공통적으로 alert 스크립트를 실행하는 페이지로 리턴
            return "common/alertAndRedirect";
        }

        // 로그인 된 상태라면 문의 작성 페이지로 이동
        return "/guide/help";
    }

    @PostMapping("/new/help")
    public String createQuestion(@RequestParam("title") String title,
                                 @RequestParam("content") String content,
                                 @RequestParam(value = "portfolioImage", required = false) List<MultipartFile> files,
                                 Principal principal) throws IOException {
        if (principal == null) {
            return "redirect:/login";
        }

        // DTO 준비
        QuestionDTO dto = new QuestionDTO();
        dto.setSubject(title);
        dto.setContent(content);

        // Service 호출
        questionService.saveQuestion(dto, files, principal.getName());

        // 저장 후 FAQ 목록으로
        return "redirect:/guide/c_ask";
    }



}
