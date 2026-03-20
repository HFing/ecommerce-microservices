package com.hfing.searchservice.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.hfing.searchservice.document.ProductDocument;
import com.hfing.searchservice.service.ProductDocumentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;

import static com.hfing.searchservice.configuration.ElasticsearchIndexInitializer.PRODUCT_INDEX;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "PRODUCT-DOCUMENT-SERVICE")
public class ProductDocumentServiceImpl implements ProductDocumentService {

    private final ElasticsearchClient elasticsearchClient;

    @Override
    public void saveProductDocument(ProductDocument document) {
        try {
            elasticsearchClient.index(i -> i
                    .index(PRODUCT_INDEX)
                    .id(document.getId())
                    .document(document)
            );

            log.info("Saved product document: {}", document.getId());
        } catch (IOException e) {
            log.error("Failed to save product document: {}", document.getId(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteProductDocument(String id) {
        try {
            elasticsearchClient.delete(i -> i
                    .index(PRODUCT_INDEX)
                    .id(id)
            );

            log.info("Deleted product document: {}", id);
        } catch (IOException e) {
            log.error("Failed to delete product document: {}", id, e);
            throw new RuntimeException(e);
        }
    }
}
