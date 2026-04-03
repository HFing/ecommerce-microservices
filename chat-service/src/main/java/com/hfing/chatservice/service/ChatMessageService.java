package com.hfing.chatservice.service;

import com.hfing.chatservice.dto.request.ChatMessageRequest;
import com.hfing.chatservice.dto.response.ChatMessageResponse;

import java.util.List;

public interface ChatMessageService {
    List<ChatMessageResponse> getMessages(String conversationId);
    ChatMessageResponse create(ChatMessageRequest request);

}
