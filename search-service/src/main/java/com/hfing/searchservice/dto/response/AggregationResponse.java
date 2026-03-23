package com.hfing.searchservice.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record AggregationResponse(
        List<CategoryCount> categories,      // Kết quả Terms Aggregation
        PriceStats priceStats,               // Kết quả Stats Aggregation
        List<PriceRangeBucket> priceRanges   // Kết quả Range Aggregation
) {
}