package com.hfing.apigateway.configuration;

import com.hfing.apigateway.client.AuthenticationClient;
import com.hfing.apigateway.dto.request.IntrospectRequest;
import com.hfing.apigateway.dto.response.ErrorResponse;
import com.hfing.apigateway.dto.PublicEndpoint;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AuthenticationFilter implements GlobalFilter, Ordered {

    // Constants
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER = "Bearer";

    // Dependencies
    private final AntPathMatcher pathMatcher = new AntPathMatcher();  // Dùng để match URL patterns
    private final JsonMapper jsonMapper;
    private final AuthenticationClient authenticationClient;

    // Public endpoints không cần authentication
    private static final List<PublicEndpoint> PUBLIC_ENDPOINTS = List.of(
            new PublicEndpoint("/api/v1/users", HttpMethod.POST),
            new PublicEndpoint("/api/v1/auth/login", HttpMethod.POST),
            new PublicEndpoint("/api/v1/auth/refresh-token", HttpMethod.POST),
            new PublicEndpoint("/api/v1/auth/introspect", HttpMethod.POST),
            new PublicEndpoint("/api/v1/search/**", HttpMethod.GET)  // Search API là public
    );

    @Override
    public Mono<Void> filter(@NonNull ServerWebExchange exchange, @NonNull GatewayFilterChain chain) {
        // 1. Lấy path và method từ request
        String path = exchange.getRequest().getURI().getPath();
        HttpMethod method = exchange.getRequest().getMethod();

        // 2. Check xem có phải public endpoint không
        if (isPublicEndpoint(path, method)) {
            return chain.filter(exchange);  // Cho phép request đi tiếp
        }

        // 3. Extract Authorization header
        List<String> authorization = exchange.getRequest().getHeaders().get(AUTHORIZATION_HEADER);
        if (authorization == null || authorization.isEmpty()) {
            return unauthenticated(exchange, "Missing Authorization header");
        }

        // 4. Check Bearer prefix
        if (!authorization.getFirst().startsWith(BEARER)) {
            return unauthenticated(exchange, "Invalid Authorization header");
        }

        // 5. Extract token (bỏ "Bearer " prefix)
        String token = authorization.getFirst().substring(7);

        // 6. Gọi User Service để introspect token
        return authenticationClient.introspect(IntrospectRequest.builder()
                        .token(token)
                        .build())
                .flatMap(introspect -> {
                    // 7. Check token có active không
                    if (introspect.data().active()) {
                        return chain.filter(exchange);  // Token hợp lệ → forward request
                    } else {
                        return unauthenticated(exchange, "Invalid token");  // Token không hợp lệ
                    }
                });
    }

    @Override
    public int getOrder() {
        return -1;  // Chạy đầu tiên trong filter chain
    }

    // Check xem path có phải public endpoint không
    private boolean isPublicEndpoint(String path, HttpMethod method) {
        return PUBLIC_ENDPOINTS.stream()
                .anyMatch(endpoint ->
                        pathMatcher.match(endpoint.getPath(), path) &&
                                (endpoint.getMethod() == null || endpoint.getMethod().equals(method))
                );
    }

    // Return 401 Unauthorized response
    private Mono<Void> unauthenticated(ServerWebExchange exchange, String message) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(HttpStatus.UNAUTHORIZED.value())
                .message(message)
                .error(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                .path(exchange.getRequest().getURI().getPath())
                .timestamp(System.currentTimeMillis())
                .build();

        try {
            byte[] bytes = jsonMapper.writeValueAsBytes(errorResponse);
            DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
            return exchange.getResponse().writeWith(Mono.just(buffer));
        } catch (Exception e) {
            return Mono.error(e);
        }
    }
}