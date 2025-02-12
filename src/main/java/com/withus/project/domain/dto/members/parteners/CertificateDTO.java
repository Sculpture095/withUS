package com.withus.project.domain.dto.members.parteners;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CertificateDTO {
    private Integer certificateIdx; // 자격증 ID 기본키
    private String certificateId; // 자격증 ID
    private Integer partnerIdx; // PartnerEntity의 partnerIdx
    private String certificateName; // 자격증 이름
    private String certificateDate; // 취득일
    private String institutionalName; // 발급기관 이름
}
