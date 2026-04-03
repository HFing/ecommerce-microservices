package com.hfing.chatservice.service.impl;

import com.hfing.chatservice.dto.request.ChatMessageRequest;
import com.hfing.chatservice.dto.response.ChatMessageResponse;
import com.hfing.chatservice.entity.ChatMessage;
import com.hfing.chatservice.entity.ParticipantInfo;
import com.hfing.chatservice.exception.ChatServiceException;
import com.hfing.chatservice.exception.ErrorCode;
import com.hfing.chatservice.mapper.ChatMessageMapper;
import com.hfing.chatservice.repository.ChatMessageRepository;
import com.hfing.chatservice.repository.ConversationRepository;
import com.hfing.chatservice.repository.httpclient.ProfileClient;
import com.hfing.chatservice.service.ChatMessageService;
import com.hfing.chatservice.service.ConversationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "CHAT-SERVICE")
public class ChatMessageServiceImpl  implements ChatMessageService {

    private final ChatMessageMapper chatMessageMapper;
    private final ConversationService conversationService;
    private final ProfileClient profileClient;
    private final ChatMessageRepository chatMessageRepository;
    private final ConversationRepository conversationRepository;

    @Override
    public List<ChatMessageResponse> getMessages(String conversationId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null)
            throw new ChatServiceException(ErrorCode.UNAUTHORIZED);

        String userId = authentication.getName();
        conversationRepository.findById(conversationId).orElseThrow(
                        () -> new ChatServiceException(ErrorCode.CONVERSATION_NOT_FOUND))
                .getParticipants().stream()
                .filter(participantInfo -> userId.equals(participantInfo.getUserId()))
                .findFirst().orElseThrow(() -> new ChatServiceException(ErrorCode.CONVERSATION_NOT_FOUND));

        var messages = chatMessageRepository.findAllByConversationIdOrderByCreatedDateDesc((conversationId));

        return messages.stream()
                .map(chatMessage -> ChatMessageResponse.builder()
                        .id(chatMessage.getId())
                        .conversationId(chatMessage.getConversationId())
                        .message(chatMessage.getMessage())
                        .sender(chatMessage.getSender())
                        .createdDate(chatMessage.getCreatedDate())
                        .me(userId.equals(chatMessage.getSender().getUserId()))
                        .build())
                .toList();
    }

    @Override
    public ChatMessageResponse create(ChatMessageRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null)
            throw new ChatServiceException(ErrorCode.UNAUTHORIZED);

        String userId = authentication.getName();
        conversationRepository.findById(request.conversationId()).orElseThrow(
                () -> new ChatServiceException(ErrorCode.CONVERSATION_NOT_FOUND))
                .getParticipants().stream()
                .filter(participantInfo -> userId.equals(participantInfo.getUserId()))
                .findFirst().orElseThrow(() -> new ChatServiceException(ErrorCode.CONVERSATION_NOT_FOUND));

        var userResponse = profileClient.getProfile(userId);
        if(Objects.isNull(userResponse))
             throw new ChatServiceException(ErrorCode.USER_NOT_FOUND);
        var userInfo = userResponse.data();


        ChatMessage chatMessage = chatMessageMapper.toChatMessage(request);
        chatMessage.setSender(ParticipantInfo.builder()
                .userId(userInfo.id())
                .firstName(userInfo.firstName())
                .lastName(userInfo.lastName())
                .avatarKey(userInfo.avatarKey())
                .build());

        chatMessage.setCreatedDate(Instant.now());

        chatMessage = chatMessageRepository.save(chatMessage);


        return ChatMessageResponse.builder()
                .id(chatMessage.getId())
                .conversationId(chatMessage.getConversationId())
                .message(chatMessage.getMessage())
                .sender(chatMessage.getSender())
                .createdDate(chatMessage.getCreatedDate())
                .me(userId.equals(chatMessage.getSender().getUserId()))
                .build();
    }

}
