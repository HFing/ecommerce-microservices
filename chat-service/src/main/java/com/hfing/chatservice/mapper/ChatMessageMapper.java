package com.hfing.chatservice.mapper;

import com.hfing.chatservice.dto.request.ChatMessageRequest;
import com.hfing.chatservice.dto.response.ChatMessageResponse;
import com.hfing.chatservice.entity.ChatMessage;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ChatMessageMapper {
    ChatMessageResponse toChatMessageResponse(ChatMessage chatMessage);

    ChatMessage toChatMessage(ChatMessageRequest request);

    List<ChatMessageResponse> toChatMessageResponses(List<ChatMessage> chatMessages);
}
