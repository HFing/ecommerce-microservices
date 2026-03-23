package com.hfing.apigateway.client;

import com.hfing.apigateway.dto.request.IntrospectRequest;
import com.hfing.apigateway.dto.response.ApiResponse;
import com.hfing.apigateway.dto.response.IntrospectResponse;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import reactor.core.publisher.Mono;


@HttpExchange
public interface AuthenticationClient {

    @PostExchange("/api/v1/auth/introspect")
    Mono<ApiResponse<IntrospectResponse>> introspect(@RequestBody IntrospectRequest request);
}
