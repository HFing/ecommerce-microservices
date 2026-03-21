package com.hfing.searchservice.dto.request;

public record SearchRequest(
        String categoryId,
        String name,
        String description,
        Double minPrice,
        Double maxPrice,
        String status,
        Boolean inStock
) {
}
