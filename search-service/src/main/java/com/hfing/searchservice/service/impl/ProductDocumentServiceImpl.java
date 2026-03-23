package com.hfing.searchservice.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.aggregations.AggregationRange;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.hfing.searchservice.document.ProductDocument;
import com.hfing.searchservice.dto.request.SearchRequest;
import com.hfing.searchservice.dto.response.*;
import com.hfing.searchservice.service.ProductDocumentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
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
            elasticsearchClient.index(i -> i.index(PRODUCT_INDEX)
                    .id(document.getId())
                    .document(document));

            log.info("Saved product document: {}", document.getId());
        } catch (IOException e) {
            log.error("Failed to save product document: {}", document.getId(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteProductDocument(String id) {
        try {
            elasticsearchClient.delete(i -> i.index(PRODUCT_INDEX).id(id));
            log.info("Deleted product document: {}", id);
        } catch (IOException e) {
            log.error("Failed to delete product document: {}", id, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public PageResponse<ProductDocument> getAllWithSearch(int page, int size, SearchRequest request) {
        List<Query> mustQueries = buildQuery(request);

        Query query = mustQueries.isEmpty() ?
                MatchAllQuery.of(m -> m)._toQuery() : BoolQuery.of(b -> b.must(mustQueries))._toQuery();

        try {
            SearchResponse<ProductDocument> response = elasticsearchClient.search(s -> s
                            .index(PRODUCT_INDEX)
                            .query(query)
                            .from((page - 1) * size)
                            .size(size),
                    ProductDocument.class
            );

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
            return PageResponse.<ProductDocument>builder()
                    .currentPage(page)
                    .pageSize(size)
                    .totalPages(0)
                    .totalElements(0)
                    .content(new ArrayList<>())
                    .build();
        }
    }

    @Override
    public AggregationResponse getAggregations(SearchRequest request) {
        List<Query> mustQueries = buildQuery(request);

        Query query = mustQueries.isEmpty() ?
                MatchAllQuery.of(m -> m)._toQuery() : BoolQuery.of(b -> b.must(mustQueries))._toQuery();

        try {
            SearchResponse<Void> response = elasticsearchClient.search(s -> s
                    .index(PRODUCT_INDEX)
                    .query(query)
                    .size(0)
                    .aggregations("by_category", a -> a.terms(t -> t.field("categoryName").size(100)))
                    .aggregations("price_stats", a -> a.stats(st -> st.field("price")))
                    .aggregations("price_ranges", a -> a.range(r -> r.field("price").ranges(
                            AggregationRange.of(rg -> rg.to(1000000.0).key("Dưới 1M")),
                            AggregationRange.of(rg -> rg.from(1000000.0).to(5000000.0).key("1M-5M")),
                            AggregationRange.of(rg -> rg.from(5000000.0).to(10000000.0).key("5M-10M")),
                            AggregationRange.of(rg -> rg.from(10000000.0).key("Trên 10M"))
                    )))
            );

            List<CategoryCount> categories = response.aggregations()
                    .get("by_category")
                    .sterms()
                    .buckets()
                    .array()
                    .stream()
                    .map(bucket -> CategoryCount.builder()
                            .name(bucket.key().stringValue())
                            .count(bucket.docCount())
                            .build())
                    .toList();

            var priceStatsAgg = response.aggregations().get("price_stats").stats();
            PriceStats priceStats = PriceStats.builder()
                    .min(priceStatsAgg.min() != null ? Math.round(priceStatsAgg.min()) : 0L)
                    .max(priceStatsAgg.max() != null ? Math.round(priceStatsAgg.max()) : 0L)
                    .avg(priceStatsAgg.avg() != null ? Math.round(priceStatsAgg.avg()) : 0L)
                    .count(priceStatsAgg.count())
                    .build();

            List<PriceRangeBucket> priceRanges = response.aggregations()
                    .get("price_ranges")
                    .range()
                    .buckets()
                    .array()
                    .stream()
                    .map(bucket -> PriceRangeBucket.builder()
                            .range(bucket.key())
                            .count(bucket.docCount())
                            .build())
                    .toList();

            return AggregationResponse.builder()
                    .categories(categories)
                    .priceStats(priceStats)
                    .priceRanges(priceRanges)
                    .build();

        }catch (Exception e) {
            log.error("Failed to get aggregations", e);
            return AggregationResponse.builder()
                    .categories(new ArrayList<>())
                    .priceStats(PriceStats.builder().build())
                    .priceRanges(new ArrayList<>())
                    .build();
        }
    }

    private List<Query> buildQuery(SearchRequest request) {
        List<Query> mustQueries = new ArrayList<>();

        if(request.name() != null && !request.name().isBlank()) {
            Query nameQuery = MatchQuery.of(m -> m
                    .field("name")
                    .query(request.name())
                    .fuzziness("AUTO")
            )._toQuery();
            mustQueries.add(nameQuery);
        }

        if(request.description() != null && !request.description().isBlank()) {
            Query descriptionQuery = MatchQuery
                    .of(m -> m
                            .field("description")
                            .query(request.description())
                            .fuzziness("AUTO")
                    )._toQuery();
            mustQueries.add(descriptionQuery);
        }

        if(request.minPrice() != null) {
            Query minPrice = RangeQuery.of(r -> r.number(n -> n.field("price")
                    .gte(request.minPrice())))._toQuery();
            mustQueries.add(minPrice);
        }

        if(request.maxPrice() != null) {
            Query maxPrice = RangeQuery.of(r -> r.number(n -> n.field("price")
                    .lte(request.maxPrice())))._toQuery();
            mustQueries.add(maxPrice);
        }

        if(request.status() != null) {
            Query statusQuery = TermQuery.of(t -> t.field("status")
                    .value(request.status()))._toQuery();
            mustQueries.add(statusQuery);
        }

        if(request.inStock() != null) {
            Query inStockQuery = TermQuery.of(t -> t.field("inStock")
                    .value(request.inStock()))._toQuery();
            mustQueries.add(inStockQuery);
        }

        return mustQueries;
    }
}
