package com.hfing.productservice.dto.response;

import com.hfing.productservice.common.ProductStatus;
import com.hfing.productservice.entity.ProductImage;
import lombok.Builder;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Builder
public record CreateProductResponse(
        String id,
        String name,
        String description,
        BigDecimal price,
        Integer quantity,
        List<ProductImage> images,
        ProductStatus status,
        Instant createdAt
) { }
