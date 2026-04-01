package com.hfing.chatservice.controller;

import com.hfing.chatservice.dto.request.ConversationRequest;
import com.hfing.chatservice.dto.response.ApiResponse;
import com.hfing.chatservice.dto.response.ConversationResponse;
import com.hfing.chatservice.service.ConversationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/conversations")
public class ConversationController {

    private final ConversationService conversationService;

    @PostMapping("/create")
    ApiResponse<ConversationResponse> createConversation(@RequestBody @Valid ConversationRequest request) {
        return ApiResponse.<ConversationResponse>builder()
                .code(HttpStatus.CREATED.value())
                .data(conversationService.createConversation(request))
                .message("Conversation created successfully")
                .build();
    }

    @GetMapping("/my-conversations")
    ApiResponse<List<ConversationResponse>> myConversations() {
        return ApiResponse.<List<ConversationResponse>>builder()
                .code(HttpStatus.OK.value())
                .message("Conversation get successfully")
                .data(conversationService.myConversations())
                .build();
    }

}
