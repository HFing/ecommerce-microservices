package com.hfing.chatservice.mapper;

import com.hfing.chatservice.dto.response.ConversationResponse;
import com.hfing.chatservice.entity.Conversation;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ConversationMapper {
    ConversationResponse toConversationResponse(Conversation conversation);

    List<ConversationResponse> toConversationResponseList(List<Conversation> conversations);
}
