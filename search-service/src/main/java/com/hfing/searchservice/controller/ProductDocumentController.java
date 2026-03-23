package com.hfing.searchservice.controller;

import com.hfing.searchservice.document.ProductDocument;
import com.hfing.searchservice.dto.request.SearchRequest;
import com.hfing.searchservice.dto.response.AggregationResponse;
import com.hfing.searchservice.dto.response.ApiResponse;
import com.hfing.searchservice.dto.response.PageResponse;
import com.hfing.searchservice.service.ProductDocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/search")
public class ProductDocumentController {

    private final ProductDocumentService productDocumentService;

    @GetMapping("/products")
    ApiResponse<PageResponse<ProductDocument>> searchProducts(
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "20") int size,
            @RequestParam(required = false) String categoryId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Boolean inStock
    ) {
        SearchRequest request = new SearchRequest(
                categoryId, name, description, minPrice, maxPrice, status, inStock
        );

        var data = productDocumentService.getAllWithSearch(page, size, request);
        return ApiResponse.<PageResponse<ProductDocument>>builder()
                .code(HttpStatus.OK.value())
                .message("Products retrieved successfully")
                .data(data)
                .build();
    }


    @GetMapping("/products/aggregations")
    ApiResponse<AggregationResponse> getAggregations(
            @RequestParam(required = false) String categoryId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Boolean inStock
    ) {
        SearchRequest request = new SearchRequest(
                categoryId, name, description, minPrice, maxPrice, status, inStock
        );

        var data = productDocumentService.getAggregations(request);
        return ApiResponse.<AggregationResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Aggregations retrieved successfully")
                .data(data)
                .build();
    }

}
