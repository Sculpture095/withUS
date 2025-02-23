package com.withus.project.service;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import com.withus.project.domain.members.PartnerEntity;
import com.withus.project.domain.projects.ProjectEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AiMatchingService {

    private final OpenAiService openAiService;

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public String getPartnerRecommendation(String projectIdStr) {

        //문자열을 UUID로 변환
        UUID projectId = UUID.fromString(projectIdStr);

        //projectId를 기준으로 프로젝트 조회
        ProjectEntity project = entityManager
                .createQuery("SELECT p FROM ProjectEntity p WHERE p.projectId = :projectId", ProjectEntity.class)
                .setParameter("projectId", projectId)
                .getSingleResult();

        if (project == null) {
            throw new IllegalArgumentException("프로젝트를 찾을수 없습니다." + projectIdStr);
        }

        //모든 파트너스 후보 조회(필요시 필터링 가능)
        List<PartnerEntity> partners = entityManager
                .createQuery("SELECT p FROM PartnerEntity p", PartnerEntity.class)
                .getResultList();

        //프로젝트와 파트너 정보를 기반으로 프롬프트 문자열 생성
        String prompt = buildPrompt(project, partners);

        //OpenAI API에 전달할 메시지 구성
        ChatMessage systemMessage = new ChatMessage("system",
                "당신은 프로젝트 매칭 전문가 AI입니다. 프로젝트 정보와 파트너 정보를 분석하여 최적의 파트너를 추천해 주세요.");
        ChatMessage userMessage = new ChatMessage("user", prompt);

        //ChatCompletionRequest 생성(모델, 파라미터 설정)
        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model("gpt-3.5-turbo")
                .messages(List.of(systemMessage, userMessage))
                .maxTokens(2048)
                .temperature(0.1)
                .build();

        //OpenAI API 호출 및 결과 반환
        ChatCompletionResult result = openAiService.createChatCompletion(request);
        return result.getChoices().get(0).getMessage().getContent();

    }

    //프로젝트와 파트너 정보를 문자열 프롬프트로 변환하는 메소드
    private String buildPrompt(ProjectEntity project, List<PartnerEntity> partners) {
        StringBuilder sb = new StringBuilder();

        // (1) 프로젝트 정보
        sb.append("프로젝트 정보:\n");
        sb.append("  - 프로젝트명: ").append(project.getProjectName()).append("\n");
        sb.append("  - 소개: ").append(project.getProjectInfo()).append("\n");
        sb.append("  - 주요 기술: ").append(project.getSelectedSkills()).append("\n");


        // (2) 파트너 후보 목록
        sb.append("파트너 후보 목록:\n");
        for (PartnerEntity partner : partners) {
            sb.append("  - ").append(partner.getMember().getNickname()).append("\n");
        }
        sb.append("\n");

        // (3) 형식 요구사항
        sb.append("위 정보를 바탕으로, 아래 형식에 맞춰 줄바꿈을 포함해 답변해 주세요:\n\n");
        sb.append("예시:\n");
        sb.append("추천하는 파트너:\n<파트너 이름>\n\n");
        sb.append("이유:\n- 첫 번째 이유\n- 두 번째 이유\n\n");
        sb.append("파트너의 주요 기술:\n<주요 기술>\n\n");
        sb.append("파트너의 경력:\n<경력>\n\n");
        sb.append("각 항목 사이에는 반드시 한 줄 이상 띄워서, 여러 줄로 작성해 주세요.");

        return sb.toString();
    }
}

