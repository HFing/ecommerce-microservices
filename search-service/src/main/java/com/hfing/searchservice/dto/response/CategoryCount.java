package com.hfing.searchservice.dto.response;

import lombok.Builder;

@Builder
public record CategoryCount(
        String name,   // Category name
        long count     // Số lượng products
) {
}
