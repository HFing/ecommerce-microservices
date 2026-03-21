package com.hfing.searchservice.service;


import com.hfing.searchservice.document.ProductDocument;
import com.hfing.searchservice.dto.request.SearchRequest;
import com.hfing.searchservice.dto.response.PageResponse;

public interface ProductDocumentService {
    void saveProductDocument(ProductDocument document);
    void deleteProductDocument(String id);
    PageResponse<ProductDocument> getAllWithSearch(int page, int size, SearchRequest request);

}