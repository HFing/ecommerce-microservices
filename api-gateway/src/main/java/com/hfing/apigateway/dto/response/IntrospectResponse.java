package com.hfing.apigateway.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.util.Set;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record IntrospectResponse(
        boolean active,
        String userId,
        Set<String> roles
) {
}
