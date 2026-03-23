package com.hfing.apigateway.configuration;

import com.hfing.apigateway.client.AuthenticationClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class HttpServiceClientConfiguration {

    @Bean
    @LoadBalanced  // Enable load balancing cho WebClient
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    public AuthenticationClient authenticationClient(WebClient.Builder webClientBuilder) {
        // 1. Build WebClient với base URL là service name
        WebClient webClient = webClientBuilder
                .baseUrl("lb://USER-SERVICE")  // lb:// = load balanced
                .build();

        // 2. Tạo HttpServiceProxyFactory từ WebClient
        HttpServiceProxyFactory factory = HttpServiceProxyFactory
                .builderFor(WebClientAdapter.create(webClient))
                .build();

        // 3. Tạo proxy instance cho AuthenticationClient
        return factory.createClient(AuthenticationClient.class);
    }
}
