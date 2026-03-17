package com.hfing.productservice.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    INTERNAL_ERROR(500, "Unexpected error occurred while processing request in backend service", HttpStatus.INTERNAL_SERVER_ERROR),

    UNAUTHORIZED(401, "Unauthorized", HttpStatus.UNAUTHORIZED),
    FORBIDDEN(403, "Forbidden", HttpStatus.FORBIDDEN),

    CATEGORY_EXISTED(400, "Category already existed", HttpStatus.BAD_REQUEST),
    CATEGORY_NOT_FOUND(404, "Category not found", HttpStatus.NOT_FOUND),

    ;

    private final int code;
    private final String message;
    private final HttpStatus httpStatus;
}
