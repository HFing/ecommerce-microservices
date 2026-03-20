package com.hfing.searchservice.document;

import lombok.*;

import java.io.Serializable;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDocument implements Serializable {
    private String id;
    private String name;
    private String description;
    private Double price;
    private String categoryId;
    private String categoryName;
    private String thumbnail;
    private String status;
    private Boolean inStock;
    private String  createdAt;
}