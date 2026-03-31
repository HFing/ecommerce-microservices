package com.hfing.chatservice.service;

import com.hfing.chatservice.dto.request.ConversationRequest;
import com.hfing.chatservice.dto.response.ConversationResponse;
import com.hfing.chatservice.entity.Conversation;

import java.util.List;

public interface ConversationService {
    List<ConversationResponse> myConversations();
    ConversationResponse createConversation(ConversationRequest request);

}
