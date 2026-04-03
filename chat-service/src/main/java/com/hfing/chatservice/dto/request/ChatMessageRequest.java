package com.hfing.chatservice.dto.request;

import jakarta.validation.constraints.NotEmpty;

public record ChatMessageRequest(
        @NotEmpty
        String conversationId,
        @NotEmpty
        String message
) {
}
