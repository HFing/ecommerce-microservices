package com.hfing.productservice.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.time.Instant;


@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record CreateCategoryResponse(
        String id,
        String name,
        String description,
        Instant createdAt
) {
}
