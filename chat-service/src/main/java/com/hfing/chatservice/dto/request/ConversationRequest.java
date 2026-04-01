package com.hfing.chatservice.dto.request;


import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record ConversationRequest(
    String type,
    @Size(min = 1, message = "At least 2 participants are required")
    @NotEmpty List<String> participantIds
) {
}
