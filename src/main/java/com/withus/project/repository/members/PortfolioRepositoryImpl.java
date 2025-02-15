package com.withus.project.repository.members;

import com.withus.project.domain.members.MemberEntity;
import com.withus.project.domain.members.PortfolioEntity;
import com.withus.project.repository.AbstractRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Transactional(transactionManager = "transactionManager")

public class PortfolioRepositoryImpl extends AbstractRepository<PortfolioEntity> {

    private final MemberRepositoryImpl memberRepository;

    public PortfolioRepositoryImpl(EntityManager entityManager, MemberRepositoryImpl memberRepository) {
        super(entityManager);
        this.memberRepository = memberRepository;
    }

    @Override
    protected Class<PortfolioEntity> getEntityClass() {
        return PortfolioEntity.class;
    }

    @Override
    protected EntityManager getEntityManager() {
        return super.getEntityManager();
    }

    public List<PortfolioEntity> findByMemberId(String memberId) {
        Integer partnerIdx = memberRepository.findPartnerIdxByMemberId(memberId);

        if (partnerIdx == null) {
            return Collections.emptyList(); // partnerIdx가 없는 경우 빈 리스트 반환
        }

        return entityManager.createQuery(
                        "SELECT p FROM PortfolioEntity p WHERE p.partner.partnerIdx = :partnerIdx", PortfolioEntity.class)
                .setParameter("partnerIdx", partnerIdx)
                .getResultList();
    }

    public Optional<PortfolioEntity> findByMemberIdAndPortfolioId(String memberId, String portfolioId) {
        // 1️⃣ memberId를 기반으로 partnerIdx 찾기
        Integer partnerIdx = memberRepository.findPartnerIdxByMemberId(memberId);

        if (partnerIdx == null) {
            return Optional.empty(); // partnerIdx가 없는 경우 빈 결과 반환
        }

        // 2️⃣ partnerIdx와 portfolioId(UUID)로 특정 포트폴리오 조회
        return entityManager.createQuery(
                        "SELECT p FROM PortfolioEntity p WHERE p.partner.partnerIdx = :partnerIdx AND p.portfolioId = :portfolioId", PortfolioEntity.class)
                .setParameter("partnerIdx", partnerIdx)
                .setParameter("portfolioId", UUID.fromString(portfolioId))
                .getResultStream()
                .findFirst();
    }

    public Optional<PortfolioEntity> findByPortfolioIdAndMemberId(String portfolioId, String memberId) {
        return entityManager.createQuery(
                        "SELECT p FROM PortfolioEntity p WHERE p.partner.member.id = :memberId AND p.portfolioId = :portfolioId",
                        PortfolioEntity.class)
                .setParameter("memberId", memberId)
                .setParameter("portfolioId", UUID.fromString(portfolioId))
                .getResultStream()
                .findFirst();
    }


    //포트폴리오 수정
    public void updatePortfolio(String memberId, String portfolioId, String title, String description) {
        Optional<PortfolioEntity> portfolioOpt = findByPortfolioIdAndMemberId(portfolioId, memberId); // ✅ 새로운 메소드 사용

        if (portfolioOpt.isPresent()) {
            PortfolioEntity portfolio = portfolioOpt.get();
            portfolio.setPortfolioTitle(title);
            portfolio.setPortfolioContext(description);
            entityManager.merge(portfolio); // ✅ 변경 내용 반영
        } else {
            throw new IllegalArgumentException("포트폴리오를 찾을 수 없습니다.");
        }
    }


    //포트폴리오 삭제
    public void deletePortfolio(String memberId, String portfolioId) {
        Optional<PortfolioEntity> portfolioOpt = findByPortfolioIdAndMemberId(portfolioId, memberId); // ✅ 새로운 메소드 사용

        if (portfolioOpt.isPresent()) {
            PortfolioEntity portfolio = portfolioOpt.get();
            entityManager.remove(entityManager.contains(portfolio) ? portfolio : entityManager.merge(portfolio));
        } else {
            throw new IllegalArgumentException("포트폴리오를 찾을 수 없습니다.");
        }
    }









}

