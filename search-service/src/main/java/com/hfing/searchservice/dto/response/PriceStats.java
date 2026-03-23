package com.hfing.searchservice.dto.response;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record PriceStats(
        long min,    // Giá thấp nhất
        long max,    // Giá cao nhất
        long avg,    // Giá trung bình
        long count         // Tổng số products
) {
}
