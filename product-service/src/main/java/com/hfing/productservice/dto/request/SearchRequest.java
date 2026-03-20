package com.hfing.productservice.dto.request;

import com.hfing.productservice.common.ProductStatus;

import java.math.BigDecimal;

public record SearchRequest(
        String categoryId,
        String name,
        BigDecimal minPrice,
        BigDecimal maxPrice,
        ProductStatus status,
        Boolean inStock
) { }