package com.hfing.chatservice.exception;

import lombok.Getter;

@Getter
public class ChatServiceException extends RuntimeException {

    private final ErrorCode errorCode;

    public ChatServiceException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}