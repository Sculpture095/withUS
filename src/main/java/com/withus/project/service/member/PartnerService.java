package com.withus.project.service.member;

import com.withus.project.domain.members.*;
import com.withus.project.repository.members.*;
import com.withus.project.repository.projects.SelectSkillRepositoryImpl;
import com.withus.project.service.file.FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PartnerService {

    private final MemberRepositoryImpl memberRepository;
    private final PartnerRepositoryImpl partnerRepository;
    private final SelectSkillRepositoryImpl selectSkillRepository;
    private final PortfolioRepositoryImpl portfolioRepository;
    private final FileUploadService fileUploadService;
    private final CertificateRepositoryImpl certificateRepository;
    private final EducationRepositoryImpl educationRepository;


    //**
    @Transactional
    public void addSkillToPartner(String memberId, SkillType skillType, Integer careerDuration) {
        selectSkillRepository.addSkillToPartner(memberId, skillType, careerDuration, partnerRepository);
    }

    //**
    public List<SelectSkillEntity> getOwnedSkills(String memberId) {
        Integer partnerIdx = partnerRepository.findPartnerIdxByMemberId(memberId);
        return selectSkillRepository.findSkillsByPartnerIdx(partnerIdx);
    }

    //**
    public List<PortfolioEntity> getPartnerPortfolios(String memberId) {
        return portfolioRepository.findByMemberId(memberId);
    }

    public PortfolioEntity getPortfolioByMemberIdAndPortfolioId(String memberId, String portfolioId) {
        return portfolioRepository.findByMemberIdAndPortfolioId(memberId, portfolioId)
                .orElseThrow(() -> new IllegalArgumentException("해당 포트폴리오가 존재하지 않거나 접근 권한이 없습니다."));
    }


    @Transactional
    public void savePortfolio(String memberId, String title, String description, LocalDate startDate, LocalDate endDate, Boolean publicOk, MultipartFile[] images) {
        PartnerEntity partner = memberRepository.findPartnerById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원은 파트너가 아닙니다."));

        PortfolioEntity portfolio = new PortfolioEntity();
        portfolio.setPartner(partner);
        portfolio.setPortfolioTitle(title);
        portfolio.setPortfolioContext(description);
        portfolio.setStartDate(startDate != null ? startDate : LocalDate.now());
        portfolio.setEndDate(endDate != null ? endDate : LocalDate.now().plusMonths(1));
        portfolio.setPublicOk(publicOk != null ? publicOk : true);

        if (images.length > 0 && !images[0].isEmpty()) {
            String imagePath = fileUploadService.storeFile(images[0]);
            portfolio.setPortfolioImg(imagePath);
        }

        portfolioRepository.save(portfolio);
    }

    @Transactional
    public void updatePortfolio(String memberId, String portfolioId, String title, String description) {
        System.out.println("📥 서비스 레이어 - Portfolio ID: " + portfolioId);

        Integer partnerIdx = memberRepository.findPartnerIdxByMemberId(memberId);
        if (partnerIdx == null) {
            throw new IllegalArgumentException("파트너 정보가 없습니다.");
        }

        portfolioRepository.updatePortfolio(memberId, portfolioId, title, description);
    }




    @Transactional
    public void deletePortfolio(String memberId, String portfolioId) {
        Integer partnerIdx = memberRepository.findPartnerIdxByMemberId(memberId);

        if (partnerIdx == null) {
            throw new IllegalArgumentException("파트너 정보가 없습니다.");
        }

        portfolioRepository.deletePortfolio(memberId, portfolioId); // ✅ memberId 전달
    }

    public List<CertificateEntity> getCertificates(String memberId) {
        return certificateRepository.findCertificatesByMemberId(memberId);
    }

    /**
     * 자격증 추가
     */
    @Transactional
    public void addCertificate(String memberId, String name, String issuer, String issueDate) {
        Integer partnerIdx = memberRepository.findPartnerIdxByMemberId(memberId); // ✅ 기존 메서드 활용
        if (partnerIdx == null) {
            throw new IllegalArgumentException("해당 회원은 파트너가 아닙니다.");
        }

        CertificateEntity certificate = new CertificateEntity();
        PartnerEntity partner = new PartnerEntity();
        partner.setPartnerIdx(partnerIdx); // partner_idx 설정

        certificate.setPartner(partner);
        certificate.setCertificateName(name);
        certificate.setInstitutionalName(issuer);
        certificate.setCertificateDate(issueDate);

        certificateRepository.saveCertificate(certificate);
    }

    /**
     * 자격증 삭제
     */
    @Transactional
    public void deleteCertificate(String certificateId) {
        certificateRepository.deleteCertificate(certificateId);
    }

    //////////////////////////////////////학력

    public PartnerEntity getEducationByMemberId(String memberId) {
        return educationRepository.findEducationByMemberId(memberId);
    }

    // 학력 추가 및 수정
    @Transactional
    public void updateEducation(String memberId, String schoolName, String major, String graduationDate) {
        educationRepository.updateEducation(memberId, schoolName, major, graduationDate);
    }

    // 학력 삭제
    @Transactional
    public void deleteEducation(String memberId) {
        educationRepository.deleteEducation(memberId);
    }

}
