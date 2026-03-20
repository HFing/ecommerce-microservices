package com.hfing.productservice.controller;

import com.hfing.productservice.dto.request.CreateProductRequest;
import com.hfing.productservice.dto.response.ApiResponse;
import com.hfing.productservice.dto.response.CreateProductResponse;
import com.hfing.productservice.dto.response.ProductDetailResponse;
import com.hfing.productservice.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;

    @PostMapping
    ApiResponse<CreateProductResponse> createProduct(@AuthenticationPrincipal Jwt jwt, @RequestBody @Valid CreateProductRequest request) {
        var data = productService.createProduct(jwt.getSubject(), request);
        return ApiResponse.<CreateProductResponse>builder()
                .code(HttpStatus.CREATED.value())
                .message("Product created successfully")
                .data(data)
                .build();
    }

    @GetMapping
    ApiResponse<List<ProductDetailResponse>> getProducts() {
        var data = productService.getAllProducts();
        return ApiResponse.<List<ProductDetailResponse>>builder()
                .code(HttpStatus.OK.value())
                .message("Products retrieved successfully")
                .data(data)
                .build();
    }

    @GetMapping("/{id}")
    ApiResponse<ProductDetailResponse> getProductById(@PathVariable String id) {
        var data = productService.getProductById(id);
        return ApiResponse.<ProductDetailResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Product retrieved successfully")
                .data(data)
                .build();
    }

    @DeleteMapping("/{id}")
    ApiResponse<Void> deleteProductById(@PathVariable String id) {
        productService.deleteProduct(id);
        return ApiResponse.<Void>builder()
                .code(HttpStatus.NO_CONTENT.value())
                .message("Product deleted successfully")
                .build();
    }
}