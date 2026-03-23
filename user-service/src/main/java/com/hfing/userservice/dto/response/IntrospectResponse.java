package com.hfing.userservice.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.util.Set;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record IntrospectResponse(
        boolean active,      // Token có còn hiệu lực không (OAuth2 standard)
        String userId,       // User ID từ token
        Set<String> roles    // Roles từ token
) {
}
