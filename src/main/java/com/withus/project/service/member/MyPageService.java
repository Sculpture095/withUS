package com.withus.project.service.member;

import com.withus.project.domain.dto.members.MyPageDTO;
import com.withus.project.domain.members.*;
import com.withus.project.domain.projects.ProjectEntity;
import com.withus.project.mapper.members.MyPageMapper;
import com.withus.project.repository.members.*;
import com.withus.project.repository.projects.ProjectRepositoryImpl;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional(transactionManager = "transactionManager")
@RequiredArgsConstructor
public class MyPageService {

    private final MyPageRepositoryImpl myPageRepository;
    private final PartnerRepositoryImpl partnerRepository;
    private final HistoryRepositoryImpl historyRepository;
    private final PortfolioRepositoryImpl portfolioRepository;
    private final MyPageMapper myPageMapper;
    private final MemberRepositoryImpl memberRepository;

    public void createDefaultMyPage(String memberId) {
        // ✅ 회원 정보 가져오기
        MemberEntity member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("회원 정보를 찾을 수 없습니다: " + memberId));

        // ✅ 이미 마이페이지가 있는지 확인 (중복 방지)
        if (myPageRepository.findSingleByMemberId(memberId).isPresent()) {
            System.out.println("⚠ [MyPageService] 이미 마이페이지가 존재함: " + memberId);
            return;
        }

        // ✅ 마이페이지 생성
        myPageRepository.createDefaultMyPage(member);
    }


    public MyPageDTO getMyPageByUserId(String id) {
        return myPageRepository.findSingleByMemberId(id)
                .map(myPageMapper::toDTO)
                .orElseThrow(() -> new EntityNotFoundException("해당 사용자의 마이페이지 정보를 찾을 수 없습니다: " + id));
    }

    //공통 필드 업데이트(주소, 프로필 등)
    public void updateCommonFields(String id, Map<String, Object> fields){
        Integer idx = myPageRepository.findIdxByMemberId(id);
        if (idx == null) {
            throw new EntityNotFoundException("사용자 ID에 해당하는 MyPage 정보를 찾을 수 없습니다: " + id);
        }
        myPageRepository.partialUpdate(id, fields);
    }

    //파트너의 학력 정보를 업데이트
    public void updatePartnerEducation(String id, Map<String, Object> fields){
        PartnerEntity partner = partnerRepository.findPartnerById(id);
        if (partner == null) {
            throw new EntityNotFoundException("파트너 정보를 찾을 수 없습니다: " + id);
        }
        partnerRepository.partialUpdate(id,fields);
    }

    //파트너의 경력 정보를 업데이트
    public void addHistory(HistoryEntity history, String id){
        PartnerEntity partner = partnerRepository.findPartnerById(id);
        history.setPartner(partner);
        historyRepository.save(history);
    }

    //파트너의 경력 정보를 업데이트
    public void updateHistory(String historyId, Map<String, Object> fields){
        historyRepository.partialUpdate(historyId, fields);
    }

    //파트너의 경력 정보를 삭제
    public void deleteHistory(String historyId){
        historyRepository.deleteById(historyId);
    }

    //파트너의 포트폴리오 추가
    public void addPortfolio(PortfolioEntity portfolio, String id){
        PartnerEntity partner = partnerRepository.findPartnerById(id);
        portfolio.setPartner(partner);
        portfolioRepository.save(portfolio);
    }

    //파트너의 포트폴리오 업데이트
    public void updatePortfolio(String portfolioId, Map<String, Object> fields){
        portfolioRepository.partialUpdate(portfolioId, fields);
    }

    //파트너의 포트폴리오 삭제
    public void deletePortfolio(String portfolioId){
        portfolioRepository.deleteById(portfolioId);
    }




//
//    // 클라이언트의 고객 사례를 관리
//    public List<ProjectEntity> manageClientCases(String id) {
//        List<ProjectEntity> projects = projectRepository.findProjectByMemberId(id);
//        projects.forEach(project -> {
//            if (project.getCases() == null || project.getCases().isEmpty()) {
//                throw new EntityNotFoundException("No cases found for project: " + project.getProjectName());
//            }
//        });
//        return projects;
//    }



}
