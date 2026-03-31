package com.hfing.chatservice.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hfing.chatservice.entity.ParticipantInfo;
import lombok.Builder;

import java.time.Instant;
import java.util.List;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ConversationResponse(
        String id,
        String type, // GROUP, DIRECT
        String participantsHash,
        String conversationAvatar,
        String conversationName,
        List<ParticipantInfo> participants,
        Instant createdDate,
        Instant modifiedDate) {
}
