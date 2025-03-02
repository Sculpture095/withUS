package com.withus.project.dto.projects;

import lombok.*;

@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class UCanSignWebhookDTO {
    private Integer ucanSignWebhookIdx;
    private String ucanSignWebhookId;
    private String documentId;
    private String eventType;
    private String payload;
    private String createdAt;
    private String contractId;

}
