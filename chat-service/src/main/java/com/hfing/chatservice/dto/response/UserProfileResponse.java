package com.hfing.chatservice.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserProfileResponse(
        String id,
        String email,
        String firstName,
        String lastName,
        String avatarKey
) {
}
