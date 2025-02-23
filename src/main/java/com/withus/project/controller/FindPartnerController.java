package com.withus.project.controller;

import com.withus.project.domain.dto.PageResponse;
import com.withus.project.domain.members.PartnerEntity;
import com.withus.project.repository.members.PartnerRepositoryImpl;
import com.withus.project.service.member.PartnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class FindPartnerController {

    private final PartnerService partnerService;
    private final PartnerRepositoryImpl partnerRepository;

    @GetMapping("/findPartner")
    public String findPartner(Model model,
                              @RequestParam(value = "page", defaultValue = "0") int page,
                              @RequestParam(value = "size", defaultValue = "5") int size,
                              // 여러 체크박스가 넘어올 수 있으므로 List<String>으로 받는다
                              @RequestParam(value = "ranks", required = false) List<String> ranks,
                              @RequestParam(value = "types", required = false) List<String> types,
                              @RequestParam(value = "sort", defaultValue = "default") String sort) {

        if (ranks == null) {
            ranks = Collections.emptyList();
        }
        if (types == null) {
            types = Collections.emptyList();
        }


        PageResponse<PartnerEntity> partnerPage =
                partnerService.getFilteredPartners(ranks, types, sort, page, size);

        int blockSize = 10;
        int startPage = (page / blockSize) * blockSize;
        int endPage = Math.min(startPage + blockSize - 1, partnerPage.getTotalPages() - 1);

        model.addAttribute("partnerPage", partnerPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        // 체크박스 선택 상태 유지를 위해 다시 모델로 전달
        model.addAttribute("ranks", ranks);
        model.addAttribute("types", types);
        model.addAttribute("sort", sort);

        return "findPartner";
    }

    // 📌 파트너 상세 페이지
    @GetMapping("/findPartner/detail/{partnerId}")
    public String getPartnerDetail(@PathVariable("partnerId") String partnerId,
                                   Model model) {
        // DB에서 파트너 정보 조회
        PartnerEntity partner = partnerRepository.findPartnerById(partnerId);
        if (partner == null) {
            // 파트너가 없으면 목록으로 리다이렉트
            return "redirect:/findPartner";
        }
        model.addAttribute("partner", partner);
        return "find_detail_partner/findPartner_detail"; // /resources/templates/findPartner_detail.html
    }

    @GetMapping("/ownSkillPage/{partnerId}")
    public String getPartnerOwnSkills(@PathVariable("partnerId")String partnerId, Model model){
        // DB에서 파트너 정보 조회
        PartnerEntity partner = partnerRepository.findPartnerById(partnerId);
        if (partner == null) {
            // 파트너가 없으면 목록으로 리다이렉트
            return "redirect:/findPartner";
        }
        model.addAttribute("partner", partner);
        return "find_detail_partner/findPartner_skill";
    }

    @GetMapping("/ownPortfolioPage/{partnerId}")
    public String getPartnerOwnPortfolios(@PathVariable("partnerId")String partnerId, Model model){
        // DB에서 파트너 정보 조회
        PartnerEntity partner = partnerRepository.findPartnerById(partnerId);
        if (partner == null) {
            // 파트너가 없으면 목록으로 리다이렉트
            return "redirect:/findPartner";
        }
        model.addAttribute("partner", partner);
        return "find_detail_partner/findPartner_portfolio";
    }



}
