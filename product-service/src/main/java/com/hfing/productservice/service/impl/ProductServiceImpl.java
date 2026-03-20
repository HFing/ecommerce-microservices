package com.hfing.productservice.service.impl;

import com.hfing.productservice.dto.request.CreateProductRequest;
import com.hfing.productservice.dto.request.SearchRequest;
import com.hfing.productservice.dto.response.CreateProductResponse;
import com.hfing.productservice.dto.response.PageResponse;
import com.hfing.productservice.dto.response.ProductDetailResponse;
import com.hfing.productservice.entity.Category;
import com.hfing.productservice.entity.Product;
import com.hfing.productservice.exception.ErrorCode;
import com.hfing.productservice.exception.ProductServiceException;
import com.hfing.productservice.repository.CategoryRepository;
import com.hfing.productservice.repository.ProductRepository;
import com.hfing.productservice.repository.specification.ProductSpecification;
import com.hfing.productservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "PRODUCT-SERVICE")
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @PreAuthorize("hasAnyAuthority('SELLER','ADMIN')")
    @Override
    public CreateProductResponse createProduct(String sellerId, CreateProductRequest request) {
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ProductServiceException(ErrorCode.CATEGORY_NOT_FOUND));

        Product product = Product.builder()
                .sellerId(sellerId)
                .name(request.name())
                .description(request.description())
                .price(request.price())
                .quantity(request.quantity())
                .images(request.images())
                .status(request.status())
                .category(category)
                .build();

        productRepository.save(product);
        log.info("Product created successfully");

        return CreateProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .quantity(product.getQuantity())
                .images(product.getImages())
                .status(product.getStatus())
                .createdAt(product.getCreatedAt())
                .build();

    }

    @Override
    public PageResponse<ProductDetailResponse> getAllProducts(int page, int size, SearchRequest request) {

        // 1. Tạo Pageable với page - 1 (JPA bắt đầu từ 0, user-friendly bắt đầu từ 1)
        // Sort theo name A-Z
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.ASC, "name"));

        // 2. Tạo Specification để filter (kết hợp các điều kiện)
        Specification<Product> specification = Specification.allOf(
                ProductSpecification.hasName(request.name()),
                ProductSpecification.hasPrice(request.minPrice(), request.maxPrice()),
                ProductSpecification.hasStatus(request.status()),
                ProductSpecification.inStock(request.inStock()),
                ProductSpecification.hasCategory(request.categoryId())
        );

        // 3. Query với Specification + Pageable
        // JPA tự động: filter, paginate, sort, và count total
        Page<Product> productPage = productRepository.findAll(specification, pageable);

        // 4. Lấy content (danh sách products của trang hiện tại)
        List<Product> products = productPage.getContent();

        // 5. Map Entity sang DTO
        List<ProductDetailResponse> responses = products.stream()
                .map(product -> ProductDetailResponse.builder()
                        .id(product.getId())
                        .name(product.getName())
                        .description(product.getDescription())
                        .price(product.getPrice())
                        .quantity(product.getQuantity())
                        .images(product.getImages())
                        .status(product.getStatus())
                        .createdAt(product.getCreatedAt())
                        .build())
                .toList();

        // 6. Build PageResponse với pagination metadata
        return PageResponse.<ProductDetailResponse>builder()
                .currentPage(page)                          // Trang user request (1, 2, 3...)
                .pageSize(pageable.getPageSize())           // Số items/trang
                .totalPages(productPage.getTotalPages())    // Tổng số trang
                .totalElements(productPage.getTotalElements()) // Tổng số items
                .content(responses)                         // Data của trang hiện tại
                .build();
    }


    @Override
    public ProductDetailResponse getProductById(String id) {
        return productRepository.findById(id)
                .map(product -> ProductDetailResponse.builder()
                        .id(product.getId())
                        .name(product.getName())
                        .description(product.getDescription())
                        .price(product.getPrice())
                        .quantity(product.getQuantity())
                        .images(product.getImages())
                        .status(product.getStatus())
                        .createdAt(product.getCreatedAt())
                        .build())
                .orElseThrow(() -> new ProductServiceException(ErrorCode.PRODUCT_NOT_FOUND));
    }

    @PreAuthorize("hasAnyAuthority('SELLER','ADMIN')")
    @Override
    public void deleteProduct(String id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductServiceException(ErrorCode.PRODUCT_NOT_FOUND));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null)
            throw new ProductServiceException(ErrorCode.UNAUTHORIZED);

        String userId = authentication.getName();

        if (!product.getSellerId().equals(userId)) {
            Set<String> authorities = authentication.getAuthorities()
                    .stream().map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toSet());

            if(!authorities.contains("ADMIN")) {
                throw new ProductServiceException(ErrorCode.PRODUCT_ACCESS_DENIED);
            }
        }

        productRepository.delete(product);
        log.info("Product deleted successfully");
    }
}




