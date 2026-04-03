package com.hfing.chatservice.controller;

import com.hfing.chatservice.dto.request.ChatMessageRequest;
import com.hfing.chatservice.dto.response.ApiResponse;
import com.hfing.chatservice.dto.response.ChatMessageResponse;
import com.hfing.chatservice.service.ChatMessageService;
import jakarta.validation.Valid;
import jakarta.ws.rs.HttpMethod;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/messages")
public class ChatMessageController {
    private final ChatMessageService chatMessageService;

    @PostMapping("/create")
    ApiResponse<ChatMessageResponse> create(
            @RequestBody @Valid ChatMessageRequest request) {
        return ApiResponse.<ChatMessageResponse>builder()
                .code(HttpStatus.CREATED.value())
                .data(chatMessageService.create(request))
                .build();
    }

    @GetMapping
    ApiResponse<List<ChatMessageResponse>> getMessages(
            @RequestParam("conversationId") String conversationId) {
        return ApiResponse.<List<ChatMessageResponse>>builder()
                .data(chatMessageService.getMessages(conversationId))
                .build();
    }
}
