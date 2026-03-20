package com.hfing.searchservice.service;

import com.hfing.searchservice.document.ProductDocument;

public interface ProductDocumentService {
    void saveProductDocument(ProductDocument document);
    void deleteProductDocument(String id);
}