package com.hfing.productservice.controller;

import com.hfing.productservice.dto.request.CreateCategoryRequest;
import com.hfing.productservice.dto.request.UpdateCategoryRequest;
import com.hfing.productservice.dto.response.ApiResponse;
import com.hfing.productservice.dto.response.CategoryDetailResponse;
import com.hfing.productservice.dto.response.CreateCategoryResponse;
import com.hfing.productservice.dto.response.UpdateCategoryResponse;
import com.hfing.productservice.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private  final CategoryService categoryService;

    @PostMapping
    ApiResponse<CreateCategoryResponse> createCategory(@RequestBody @Valid CreateCategoryRequest request) {
        var data = categoryService.createCategory(request);
        return ApiResponse.<CreateCategoryResponse>builder()
                .code(HttpStatus.CREATED.value())
                .message("Category created successfully")
                .data(data)
                .build();
    }

    @GetMapping
    ApiResponse<List<CategoryDetailResponse>> getCategory() {
        var date = categoryService.getCategories();
        return ApiResponse.<List<CategoryDetailResponse>>builder()
                .code(HttpStatus.OK.value())
                .message("Get categories successfully")
                .data(date)
                .build();
    }


    @PutMapping("/{id}")
    ApiResponse<UpdateCategoryResponse> updateCategory(@PathVariable String id, @RequestBody @Valid UpdateCategoryRequest request) {
        var data = categoryService.updateCategory(id, request);
        return ApiResponse.<UpdateCategoryResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Category updated successfully")
                .data(data)
                .build();
    }

    @DeleteMapping("/{id}")
    ApiResponse<Void> deleteCategory(@PathVariable String id) {
        categoryService.deleteCategory(id);
        return ApiResponse.<Void>builder()
                .code(HttpStatus.NO_CONTENT.value())
                .message("Category deleted successfully")
                .build();
    }
}