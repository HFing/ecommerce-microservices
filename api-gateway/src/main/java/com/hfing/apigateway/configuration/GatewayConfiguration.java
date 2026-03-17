package com.hfing.apigateway.configuration;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfiguration {

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("user-service", r -> r.path("/api/v1/users/**", "/api/v1/auth/**")
                        .uri("lb://USER-SERVICE"))
                .route("product-service", r -> r.path("/api/v1/categories/**", "/api/v1/products/**")
                        .uri("lb://PRODUCT-SERVICE"))
                .build();
    }
}