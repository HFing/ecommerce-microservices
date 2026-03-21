package com.hfing.searchservice.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.hfing.searchservice.document.ProductDocument;
import com.hfing.searchservice.dto.request.SearchRequest;
import com.hfing.searchservice.dto.response.PageResponse;
import com.hfing.searchservice.service.ProductDocumentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    @Override
    public PageResponse<ProductDocument> getAllWithSearch(int page, int size, SearchRequest request) {
        List<Query> mustQueries = new ArrayList<>();

        // 1. Search by name (full-text search với fuzziness)
        if (request.name() != null && !request.name().isBlank()) {
            Query nameQuery = MatchQuery.of(m -> m
                    .field("name")
                    .query(request.name())
                    .fuzziness("AUTO")  // Cho phép typo
            )._toQuery();
            mustQueries.add(nameQuery);
        }

        // 2. Search by description (full-text search với fuzziness)
        if (request.description() != null && !request.description().isBlank()) {
            Query descriptionQuery = MatchQuery.of(m -> m
                    .field("description")
                    .query(request.description())
                    .fuzziness("AUTO")
            )._toQuery();
            mustQueries.add(descriptionQuery);
        }

        // 3. Filter by categoryId (exact match)
        if (request.categoryId() != null && !request.categoryId().isBlank()) {
            Query categoryQuery = TermQuery.of(t -> t
                    .field("categoryId")
                    .value(request.categoryId())
            )._toQuery();
            mustQueries.add(categoryQuery);
        }

        // 4. Filter by price range
        if (request.minPrice() != null) {
            Query minPriceQuery = RangeQuery.of(r -> r
                    .number(n -> n
                            .field("price")
                            .gte(request.minPrice())  // Greater than or equal
                    )
            )._toQuery();
            mustQueries.add(minPriceQuery);
        }

        if (request.maxPrice() != null) {
            Query maxPriceQuery = RangeQuery.of(r -> r
                    .number(n -> n
                            .field("price")
                            .lte(request.maxPrice())  // Less than or equal
                    )
            )._toQuery();
            mustQueries.add(maxPriceQuery);
        }

        // 5. Filter by status (exact match)
        if (request.status() != null && !request.status().isBlank()) {
            Query statusQuery = TermQuery.of(t -> t
                    .field("status")
                    .value(request.status())
            )._toQuery();
            mustQueries.add(statusQuery);
        }

        // 6. Filter by inStock (exact match)
        if (request.inStock() != null) {
            Query inStockQuery = TermQuery.of(t -> t
                    .field("inStock")
                    .value(request.inStock())
            )._toQuery();
            mustQueries.add(inStockQuery);
        }

        // Build final query
        Query finalQuery = mustQueries.isEmpty()
                ? MatchAllQuery.of(m -> m)._toQuery()
                : BoolQuery.of(b -> b.must(mustQueries))._toQuery();

        try {
            // Execute search
            SearchResponse<ProductDocument> response = elasticsearchClient.search(s -> s
                            .index(PRODUCT_INDEX)
                            .query(finalQuery)
                            .from((page - 1) * size)  // page bắt đầu từ 1
                            .size(size),
                    ProductDocument.class
            );

            // Build response
            List<ProductDocument> products = response.hits().hits().stream()
                    .map(Hit::source)
                    .toList();

            long totalElements = response.hits().total() != null
                    ? response.hits().total().value()
                    : 0;

            int totalPages = (int) Math.ceil((double) totalElements / size);

            return PageResponse.<ProductDocument>builder()
                    .currentPage(page)
                    .pageSize(size)
                    .totalPages(totalPages)
                    .totalElements(totalElements)
                    .content(products)
                    .build();

        } catch (IOException e) {
            log.error("Failed to search products", e);
            return PageResponse.<ProductDocument>builder()
                    .currentPage(page)
                    .pageSize(size)
                    .totalPages(0)
                    .totalElements(0)
                    .content(new ArrayList<>())
                    .build();
        }
    }




}
