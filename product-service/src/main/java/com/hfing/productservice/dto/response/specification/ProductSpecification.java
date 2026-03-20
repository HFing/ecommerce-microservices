package com.hfing.productservice.dto.response.specification;

import com.hfing.productservice.common.ProductStatus;
import com.hfing.productservice.entity.Category;
import com.hfing.productservice.entity.Product;
import io.micrometer.common.util.StringUtils;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public final class ProductSpecification {
    private ProductSpecification() {
    }

    public static Specification<Product> hasName(String name) {
        if(StringUtils.isBlank(name)) {
            return (_, _, criteriaBuilder) -> criteriaBuilder.conjunction();
        } else {
            return (root, _, criteriaBuilder) -> criteriaBuilder
                    .like(root.get("name"), "%" + name + "%");
        }
    }

    public static Specification<Product> hasPrice(BigDecimal minPrice, BigDecimal maxPrice) {
        if(minPrice == null && maxPrice == null) {
            return (_, _, criteriaBuilder) -> criteriaBuilder.conjunction();
        }
        else if(minPrice != null && maxPrice == null) {
            return (root, _, criteriaBuilder) -> criteriaBuilder
                    .greaterThanOrEqualTo(root.get("price"), minPrice);
        } else if(minPrice == null) {
            return (root, _, criteriaBuilder) -> criteriaBuilder
                    .lessThanOrEqualTo(root.get("price"), maxPrice);
        } else {
            return (root, _, criteriaBuilder) -> criteriaBuilder
                    .between(root.get("price"), minPrice, maxPrice);
        }
    }

    public static Specification<Product> hasStatus(ProductStatus status) {
        if(status == null) {
            return (_, _, criteriaBuilder) -> criteriaBuilder.conjunction();
        } else {
            return (root, _, criteriaBuilder) -> criteriaBuilder
                    .equal(root.get("status"), status);
        }
    }

    public static Specification<Product> inStock(Boolean inStock) {
        if(inStock == null) {
            return (_, _, criteriaBuilder) -> criteriaBuilder.conjunction();
        } else if(inStock) {
            return (root, _, criteriaBuilder) -> criteriaBuilder
                    .greaterThanOrEqualTo(root.get("quantity"), 1);
        } else {
            return (root, _, criteriaBuilder) -> criteriaBuilder
                    .equal(root.get("quantity"), 0);
        }
    }

    public static Specification<Product> hasCategory(String categoryId) {
        if(StringUtils.isBlank(categoryId)) {
            return (_, _, criteriaBuilder) -> criteriaBuilder.conjunction();
        } else {
            return (root, _, criteriaBuilder) -> {
                Join<Product, Category> categoryJoin = root.join("category");
                return criteriaBuilder.equal(categoryJoin.get("id"), categoryId);
            };
        }
    }

}
