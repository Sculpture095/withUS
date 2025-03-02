package com.withus.project.service.member;

import com.withus.project.dto.PageResponse;
import com.withus.project.domain.members.*;
import com.withus.project.repository.members.*;
import com.withus.project.repository.projects.SelectSkillRepositoryImpl;
import com.withus.project.service.other.FileUploadService;
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
    private final HistoryRepositoryImpl historyRepository;


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
    public void savePortfolio(String memberId,
                              String title,
                              String description,
                              LocalDate startDate,
                              LocalDate endDate,
                              Boolean publicOk,
                              MultipartFile[] images) {
        PartnerEntity partner = memberRepository.findPartnerById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원은 파트너가 아닙니다."));

        // PortfolioEntity 생성
        PortfolioEntity portfolio = new PortfolioEntity();
        portfolio.setPartner(partner);
        portfolio.setPortfolioTitle(title);
        portfolio.setPortfolioContext(description);
        portfolio.setStartDate(startDate != null ? startDate : LocalDate.now());
        portfolio.setEndDate(endDate != null ? endDate : LocalDate.now().plusMonths(1));
        portfolio.setPublicOk(publicOk != null ? publicOk : true);

        // ✅ 이미지 업로드 (배열의 첫 번째 파일만)
        if (images.length > 0 && !images[0].isEmpty()) {
            String imagePath = fileUploadService.storeFile(images[0]);
            portfolio.setPortfolioImg(imagePath);
        }

        // DB 저장
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

    //-------------------------------경력
    public List<HistoryEntity> getPartnerHistories(String memberId) {
        return historyRepository.findAllHistoryByMemberId(memberId);
    }

    // 경력 추가
    @Transactional
    public void addHistory(String memberId, String companyName, LocalDate joinDate, LocalDate exitDate, String work) {
        Integer partnerIdx = memberRepository.findPartnerIdxByMemberId(memberId);
        if (partnerIdx == null) {
            throw new IllegalArgumentException("파트너 정보를 찾을 수 없습니다.");
        }

        PartnerEntity partner = partnerRepository.findPartnerById(memberId);
        if (partner == null) {
            throw new IllegalArgumentException("파트너 엔터티를 찾을 수 없습니다.");
        }

        HistoryEntity history = new HistoryEntity();
        history.setPartner(partner);
        history.setCompanyName(companyName);
        history.setJoinDate(joinDate);
        history.setExitDate(exitDate);
        history.setWork(work);

        historyRepository.saveHistory(history);
    }

    @Transactional
    public void deleteHistory(String memberId, String historyId) {
        historyRepository.deleteHistoryByMemberId(memberId, historyId);
    }

    @Transactional(readOnly = true)
    public PageResponse<PartnerEntity> getAllPartners(int page, int size) {
        int offset = page*size;
        List<PartnerEntity> content = partnerRepository.findAllPartners(offset, size);
        long totalElements = partnerRepository.countAllPartners();
        int totalPages = (int)Math.ceil((double)totalElements/size);
        return new PageResponse<>(content,page,totalPages,totalElements);
    }

    @Transactional(readOnly = true)
    public PageResponse<PartnerEntity> getAllPartners(String keyword,String sort, int page, int size) {
        int offset = page * size;
        List<PartnerEntity> content;
        long totalElements;

        if (keyword == null || keyword.trim().isEmpty()) {
            if ("portfolio".equals(sort)) {
                content = partnerRepository.findAllPartnersOrderByPortfolioCount(offset, size);
            } else if ("rank".equals(sort)) {
                content = partnerRepository.findAllPartnersOrderByRank(offset, size);
            } else {
                content = partnerRepository.findAllPartners(offset, size);
            }
            totalElements = partnerRepository.countAllPartners();
        } else {
            if ("portfolio".equals(sort)) {
                content = partnerRepository.findAllPartnersByKeywordOrderByPortfolioCount(keyword, offset, size);
            } else if ("rank".equals(sort)) {
                content = partnerRepository.findAllPartnersByKeywordOrderByRank(keyword, offset, size);
            } else {
                content = partnerRepository.findAllPartners(keyword, offset, size);
            }
            totalElements = partnerRepository.countAllPartners(keyword);
        }

        int totalPages = (int) Math.ceil((double) totalElements / size);
        return new PageResponse<>(content, page, totalPages, totalElements);
    }

    @Transactional(readOnly = true)
    public PageResponse<PartnerEntity> getFilteredPartners(
            List<String> ranks,
            List<String> types,
            String sort,
            int page,
            int size
    ) {
        int offset = page * size;

        // 레포지토리에서 동적으로 JPQL을 만들어 조회
        List<PartnerEntity> content = partnerRepository.findPartnersByFilter(ranks, types, sort, offset, size);
        long totalElements = partnerRepository.countPartnersByFilter(ranks, types);

        int totalPages = (int) Math.ceil((double) totalElements / size);

        return new PageResponse<>(content, page, totalPages, totalElements);
    }



}
