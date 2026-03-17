package com.hfing.productservice.service;

import com.hfing.productservice.dto.request.CreateCategoryRequest;
import com.hfing.productservice.dto.request.UpdateCategoryRequest;
import com.hfing.productservice.dto.response.CategoryDetailResponse;
import com.hfing.productservice.dto.response.CreateCategoryResponse;
import com.hfing.productservice.dto.response.UpdateCategoryResponse;

import java.util.List;

public interface CategoryService {
    CreateCategoryResponse createCategory(CreateCategoryRequest request);
    List<CategoryDetailResponse> getCategories();
    UpdateCategoryResponse updateCategory(String id, UpdateCategoryRequest request);
    void deleteCategory(String id);
}
