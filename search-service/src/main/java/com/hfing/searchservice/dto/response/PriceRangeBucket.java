package com.hfing.searchservice.dto.response;

import lombok.Builder;

@Builder
public record PriceRangeBucket(
        String range,   // Price range label
        long count      // Số lượng products
) {
}
