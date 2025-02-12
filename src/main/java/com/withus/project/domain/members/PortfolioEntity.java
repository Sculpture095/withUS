package com.withus.project.domain.members;

import com.withus.project.config.UUIDToStringConverter;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "portfolio",
        uniqueConstraints = @UniqueConstraint(name = "uk_portfolio_id", columnNames = "portfolio_id"))
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PortfolioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "portfolio_idx")
    private Integer portfolioIdx;

    @Column(name = "portfolio_id", columnDefinition = "CHAR(36)", unique = true, nullable = false, updatable = false)
    @Convert(converter = UUIDToStringConverter.class)
    private UUID portfolioId;

    @ManyToOne
    @JoinColumn(name = "partner_idx", nullable = false,foreignKey = @ForeignKey(name = "fk_portfolio_partner"))
    private PartnerEntity partner; // PartnerEntity를 참조하는 외래키

    @Column(name = "portfoliotitle", length = 30)
    private String portfolioTitle; // 포트폴리오 제목

    @Column(name = "portfoliocontext", length = 2000)
    private String portfolioContext; // 포트폴리오 설명

    @Column(name = "publicok", nullable = false)
    private Boolean publicOk; // 공개 여부

    @Column(name = "startdate", nullable = false)
    private LocalDate startDate; // 시작일자

    @Column(name = "enddate", nullable = false)
    private LocalDate endDate; // 종료일자

    @Column(name = "portfolioimg", length = 500)
    private String portfolioImg; // 포트폴리오 이미지

    @Column(name = "portfoliourl", length = 100)
    private String resultUrl; // 결과 URL

    @Column(name = "thumbnail", length = 300)
    private String thumbnail; // 썸네일 URL

    @PrePersist
    public void prePersist() {
        if (this.portfolioId == null){
            this.portfolioId = UUID.randomUUID();
        }
    }

}
