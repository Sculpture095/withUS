package com.withus.project.controller;

import com.withus.project.domain.dto.members.MemberDTO;
import com.withus.project.domain.dto.members.parteners.PortfolioDTO;
import com.withus.project.domain.members.*;
import com.withus.project.repository.members.HistoryRepositoryImpl;
import com.withus.project.repository.members.PortfolioRepositoryImpl;
import com.withus.project.repository.projects.SelectSkillRepositoryImpl;
import com.withus.project.service.member.PartnerService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@RequestMapping("/partner")
public class PartnerController {

    private final PartnerService partnerService;
    private final PortfolioRepositoryImpl portfolioRepository;
    private final HistoryRepositoryImpl historyRepository;

    @GetMapping("/skills")
    public String getPartnerSkills(HttpSession session, Model model){
        MemberDTO member = getMemberId(session);

        if (member == null) {
            return "redirect:/login";
        }
        List<SkillType> skillList = Arrays.asList(SkillType.values());
        List<SelectSkillEntity> ownSkills = partnerService.getOwnedSkills(member.getId());

        model.addAttribute("member", member);
        model.addAttribute("skillList", skillList);
        model.addAttribute("ownSkills", ownSkills);

        return "partner_myPage/p_skill";
    }

    @PostMapping("/add-skill")
    public ResponseEntity<String> addSkill(@RequestBody Map<String, Object> requestData, HttpSession session) {
        MemberDTO member = getMemberId(session);

        if (member == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        String skillTypeStr = (String) requestData.get("skillType");
        Integer careerDuration = (Integer) requestData.get("careerDuration");

        try {
            SkillType skillType = SkillType.valueOf(skillTypeStr.toUpperCase());
            partnerService.addSkillToPartner(member.getId(), skillType, careerDuration);
            return ResponseEntity.ok("기술이 추가되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("올바르지 않은 기술 정보입니다.");
        }
    }

    @GetMapping("/portfolio")
    public String getPartnerPortfolio(HttpSession session, Model model) {
        MemberDTO member = getMemberId(session);

        if (member == null) {
            return "redirect:/login";
        }

        List<PortfolioEntity> portfolios = partnerService.getPartnerPortfolios(member.getId());

        model.addAttribute("member", member);
        model.addAttribute("portfolios", portfolios);

        return "partner_myPage/p_portfolio";
    }

    @GetMapping("/portfolio/detail/{portfolioId}")
    public String getPortfolioDetail(@PathVariable String portfolioId, HttpSession session, Model model) {
        MemberDTO member = getMemberId(session);

        if (member == null) {
            return "redirect:/login";
        }

        PortfolioEntity portfolio = partnerService.getPortfolioByMemberIdAndPortfolioId(member.getId(), portfolioId);

        if (portfolio == null) {
            System.out.println("❌ 포트폴리오 데이터를 찾을 수 없습니다.");
            return "redirect:/partner/portfolio"; // 목록으로 이동
        }

        System.out.println("📥 포트폴리오 ID 전달됨: " + portfolio.getPortfolioId());

        model.addAttribute("member", member);
        model.addAttribute("portfolio", portfolio);

        return "partner_myPage/p_portfolio_detail";
    }




    @GetMapping("/portfolio/add")
    public String showPortfolioAddPage(HttpSession session, Model model) {
        MemberDTO member = getMemberId(session);

        if (member == null) {
            return "redirect:/login";
        }

        model.addAttribute("member", member);

        return "partner_myPage/p_portfolio_add";  // ✅ Thymeleaf 템플릿 경로
    }

    @PostMapping("/portfolio/add")
    public ResponseEntity<?> savePortfolio(
                                           @RequestParam("portfolioTitle") String portfolioTitle,
                                           @RequestParam("portfolioContext") String portfolioContext,
                                           @RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                           @RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                                           @RequestParam("publicOk") Boolean publicOk,
                                           @RequestParam(value = "portfolioImg", required = false) MultipartFile[] portfolioImg, HttpSession session) {

        // 🔍 Spring Security를 통한 로그인 체크
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        // ✅ 로그인된 사용자 ID 가져오기
        MemberDTO member = getMemberId(session);

        // ✅ 포트폴리오 저장 로직 실행
        partnerService.savePortfolio(member.getId(), portfolioTitle, portfolioContext, startDate, endDate, publicOk, portfolioImg);

        return ResponseEntity.ok("포트폴리오 저장 성공");
    }

    //포트폴리오 수정
    @PostMapping("/portfolio/update")
    @ResponseBody
    public ResponseEntity<?> updatePortfolio(@RequestBody PortfolioDTO portfolioDTO, HttpSession session) {
        MemberDTO member = getMemberId(session);

        if (member == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        System.out.println("📥 업데이트 요청 수신 - Portfolio ID: " + portfolioDTO.getPortfolioId());
        System.out.println("📥 새로운 제목: " + portfolioDTO.getPortfolioTitle());
        System.out.println("📥 새로운 내용: " + portfolioDTO.getPortfolioContext());

        try {
            partnerService.updatePortfolio(member.getId(), portfolioDTO.getPortfolioId(), portfolioDTO.getPortfolioTitle(), portfolioDTO.getPortfolioContext());
            return ResponseEntity.ok("포트폴리오가 성공적으로 수정되었습니다.");
        } catch (Exception e) {
            System.err.println("❌ 업데이트 오류: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("수정 실패: " + e.getMessage());
        }
    }

    //포트폴리오 삭제
    @DeleteMapping("/portfolio/delete/{portfolioId}")
    @ResponseBody
    public ResponseEntity<?> deletePortfolio(@PathVariable String portfolioId, HttpSession session) {
        MemberDTO member = getMemberId(session);

        if (member == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        System.out.println("🗑 삭제 요청 수신 - Portfolio ID: " + portfolioId);
        System.out.println("🗑 사용자 ID: " + member);

        try {
            partnerService.deletePortfolio(member.getId(), portfolioId);
            return ResponseEntity.ok("포트폴리오가 성공적으로 삭제되었습니다.");
        } catch (Exception e) {
            System.err.println("❌ 삭제 오류: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("삭제 실패: " + e.getMessage());
        }
    }

    //자격증 페이지
    @GetMapping("/certificate")
    public String getCertificatePage(HttpSession session, Model model) {
        MemberDTO member = getMemberId(session);

        if (member == null) {
            return "redirect:/login";
        }

        List<CertificateEntity> certificates = partnerService.getCertificates(member.getId());
        model.addAttribute("member", member);
        model.addAttribute("certificates", certificates);

        return "partner_myPage/p_certificate";
    }

    //자격증 추가
    @PostMapping("/certificate/add")
    public ResponseEntity<String> addCertificate(
            @RequestParam String certificateName,
            @RequestParam String issuer,
            @RequestParam String issueDate,
            HttpSession session) {

        MemberDTO member = getMemberId(session);
        if (member == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        try {
            partnerService.addCertificate(member.getId(), certificateName, issuer, issueDate);
            return ResponseEntity.ok("자격증이 추가되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("추가 실패: " + e.getMessage());
        }
    }

    //자격증 삭제
    @DeleteMapping("/certificate/delete/{certificateId}")
    public ResponseEntity<String> deleteCertificate(@PathVariable String certificateId, HttpSession session) {
        MemberDTO member = getMemberId(session);
        if (member == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        try {
            partnerService.deleteCertificate(certificateId);
            return ResponseEntity.ok("자격증이 삭제되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("삭제 실패: " + e.getMessage());
        }
    }

    //---------------------------학력--------------------------------
    @GetMapping("/education")
    public String getEducationPage(HttpSession session, Model model) {
        MemberDTO member = getMemberId(session);

        if (member == null) {
            return "redirect:/login";
        }

        PartnerEntity partner = partnerService.getEducationByMemberId(member.getId());
        model.addAttribute("member", member);
        model.addAttribute("partner", partner);

        return "partner_myPage/p_education";
    }

    @PostMapping("/education/update")
    public ResponseEntity<String> updateEducation(
            @RequestParam String schoolName,
            @RequestParam String major,
            @RequestParam String graduationDate,
            HttpSession session) {

        MemberDTO member = getMemberId(session);
        if (member == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        try {
            partnerService.updateEducation(member.getId(), schoolName, major, graduationDate);
            return ResponseEntity.ok("학력이 업데이트되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("수정 실패: " + e.getMessage());
        }
    }

    @DeleteMapping("/education/delete")
    public ResponseEntity<String> deleteEducation(HttpSession session) {
        MemberDTO member = getMemberId(session);
        if (member == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        try {
            partnerService.deleteEducation(member.getId());
            return ResponseEntity.ok("학력이 삭제되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("삭제 실패: " + e.getMessage());
        }
    }

    //---------------------경력
    @GetMapping("/history")
    public String getHistory(HttpSession session, Model model) {
        MemberDTO member = getMemberId(session);
        if (member == null) {
            return "redirect:/login";
        }
        List<HistoryEntity> histories = partnerService.getPartnerHistories(member.getId());
        model.addAttribute("member", member);
        model.addAttribute("histories", histories);
        return "partner_myPage/p_history";

    }

    @PostMapping("/history/add")
    public ResponseEntity<String> addHistory(
            @RequestParam String companyName,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate joinDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate exitDate,
            @RequestParam String work,
            HttpSession session) {

        MemberDTO member = getMemberId(session);
        if (member == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        try {
            partnerService.addHistory(member.getId(), companyName, joinDate, exitDate, work);
            return ResponseEntity.ok("경력이 추가되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("추가 실패: " + e.getMessage());
        }
    }

    @DeleteMapping("/history/delete/{historyId}")
    public ResponseEntity<String> deleteHistory(@PathVariable String historyId, HttpSession session) {
        MemberDTO member = getMemberId(session);  // ✅ 따로 분리하여 호출

        try {
            partnerService.deleteHistory(member.getId(), historyId);
            return ResponseEntity.ok("경력이 성공적으로 삭제되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("경력 삭제 실패: " + e.getMessage());
        }
    }

    // ✅ Member ID 가져오는 로직을 별도 메서드로 분리
    private MemberDTO getMemberId(HttpSession session) {
        return (MemberDTO) session.getAttribute("member");
    }











}
