package com.hfing.productservice.service;

import com.hfing.productservice.dto.request.CreateProductRequest;
import com.hfing.productservice.dto.response.CreateProductResponse;
import com.hfing.productservice.dto.response.ProductDetailResponse;

import java.util.List;

public interface ProductService {
    CreateProductResponse createProduct(String sellerId, CreateProductRequest request);
    List<ProductDetailResponse> getAllProducts();
    ProductDetailResponse getProductById(String id);
    void deleteProduct(String id);
}
