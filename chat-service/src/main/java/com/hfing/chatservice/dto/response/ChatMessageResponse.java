package com.hfing.chatservice.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hfing.chatservice.entity.ParticipantInfo;
import lombok.Builder;

import java.time.Instant;


@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ChatMessageResponse(
        String id,
        String conversationId,
        boolean me,
        String message,
        ParticipantInfo sender,
        Instant createdDate
) {
}
