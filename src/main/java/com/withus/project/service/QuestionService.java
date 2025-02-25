package com.withus.project.service;

import com.withus.project.domain.dto.members.QuestionDTO;
import com.withus.project.domain.members.MemberEntity;
import com.withus.project.domain.members.QuestionEntity;
import com.withus.project.repository.members.QuestionRepositoryImpl;
import com.withus.project.service.file.FileUploadService;
import com.withus.project.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class QuestionService {

    private final QuestionRepositoryImpl questionRepository;
    private final MemberService memberService;  // 로그인 사용자 → MemberEntity 조회 가정
    private final FileUploadService fileUploadService;


    public void saveQuestion(QuestionDTO questionDTO,
                             List<MultipartFile> files,
                             String memberId) throws IOException {
        // (1) MemberEntity 조회
        MemberEntity member = memberService.findByMemberId(memberId);
        if (member == null) {
            throw new IllegalStateException("로그인 사용자 정보를 찾을 수 없습니다.");
        }

        // (2) QuestionEntity 생성 & 필드 매핑
        QuestionEntity questionEntity = new QuestionEntity();
        questionEntity.setMember(member);                  // 작성자(FK)
        questionEntity.setSubject(questionDTO.getSubject());
        questionEntity.setContent(questionDTO.getContent());
        // isAnswered / questionId는 @PrePersist로 자동 설정

        // (3) 파일 업로드 (예: 여러 개 중 첫 번째 파일만 DB에 저장)
        if (files != null && !files.isEmpty()) {
            MultipartFile file = files.get(0);
            if (!file.isEmpty()) {
                // FileUploadService 사용
                String dbFilePath = fileUploadService.storeFile(file);
                // storeFile(...)이 "/uploads/랜덤파일명.확장자" 형태를 리턴한다고 가정
                questionEntity.setAttachment(dbFilePath);
            }
        }

        // (4) DB 저장
        questionRepository.save(questionEntity);
    }

}
