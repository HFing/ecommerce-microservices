package com.hfing.productservice.dto.response;

import lombok.Builder;

import java.time.Instant;

@Builder
public record CategoryDetailResponse (
        String id,
        String name,
        String description,
        Instant createdAt
){
}
