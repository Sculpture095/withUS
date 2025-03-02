package com.withus.project.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j
public class HomeController {

    @GetMapping("/")
    public String mainPage() {
        log.info("home controller 실행됨");
        return "main";
    }
    @GetMapping("/main")
    public String home() {
        log.info("home controller 실행됨");
        return "main";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }


    @GetMapping("/signup")
    public String signupForm(HttpServletRequest request, Model model) {
        String alertMessage = (String) request.getSession().getAttribute("alertMessage");
        if (alertMessage != null) {
            model.addAttribute("alertMessage", alertMessage);
            request.getSession().removeAttribute("alertMessage");
        }
        return "signup"; // templates/signup.html
    }
}
