package com.hfing.userservice.configuration;

import com.hfing.userservice.exception.ErrorCode;
import com.hfing.userservice.exception.ErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final JsonMapper jsonMapper;

    @Override
    public void handle(@NonNull HttpServletRequest request,
                       @NonNull HttpServletResponse response,
                       @NonNull AccessDeniedException accessDeniedException)
            throws IOException, ServletException {

        // 1. Lấy error code FORBIDDEN (403)
        ErrorCode errorCode = ErrorCode.FORBIDDEN;

        // 2. Set HTTP status code
        response.setStatus(errorCode.getCode());

        // 3. Set content type là JSON
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        // 4. Tạo ErrorResponse object
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .error(errorCode.getHttpStatus().getReasonPhrase())
                .path(request.getRequestURI())
                .timestamp(System.currentTimeMillis())
                .build();

        // 5. Convert ErrorResponse → JSON và write vào response
        response.getWriter().write(jsonMapper.writeValueAsString(errorResponse));

        // 6. Flush buffer
        response.flushBuffer();
    }
}
