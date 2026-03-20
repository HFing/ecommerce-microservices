package com.hfing.searchservice.consumer;

import com.hfing.event.ProductCreatedEvent;
import com.hfing.event.ProductDeletedEvent;
import com.hfing.searchservice.document.ProductDocument;
import com.hfing.searchservice.service.ProductDocumentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j(topic = "PRODUCT-EVENT-CONSUMER")
@KafkaListener(topics = "product-events", groupId = "search-service-consumer")
public class ProductEventConsumer {

    private final ProductDocumentService productDocumentService;

    @KafkaHandler
    public void consumeProductCreatedEvent(@Payload ProductCreatedEvent event) {
        log.info("Consumed ProductCreatedEvent: {}", event.getId());
        productDocumentService.saveProductDocument(convertToDocument(event));
        log.info("Indexed product successfully: {}", event.getId());
    }

    @KafkaHandler
    public void consumeProductDeletedEvent(@Payload ProductDeletedEvent event) {
        log.info("Consumed ProductDeletedEvent: {}", event.getId());
        productDocumentService.deleteProductDocument(event.getId());
        log.info("Deleted product successfully: {}", event.getId());
    }

    private ProductDocument convertToDocument(ProductCreatedEvent event) {
        return ProductDocument.builder()
                .id(event.getId())
                .name(event.getName())
                .description(event.getDescription())
                .price(event.getPrice())
                .categoryId(event.getCategoryId())
                .categoryName(event.getCategoryName())
                .thumbnail(event.getThumbnail())
                .status(event.getStatus())
                .inStock(event.getInStock())
                .createdAt(event.getCreatedAt().toString())
                .build();
    }
}