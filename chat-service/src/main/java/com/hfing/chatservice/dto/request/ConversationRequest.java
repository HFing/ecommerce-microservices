package com.hfing.chatservice.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record ConversationRequest(
    String type,
    @Min(value = 1, message = "At least 2 participants are required")
    @NotEmpty List<String> participantIds
) {
}
