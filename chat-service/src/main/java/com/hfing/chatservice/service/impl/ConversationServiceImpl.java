package com.hfing.chatservice.service.impl;


import com.hfing.chatservice.dto.request.ConversationRequest;
import com.hfing.chatservice.dto.response.ConversationResponse;
import com.hfing.chatservice.entity.Conversation;
import com.hfing.chatservice.entity.ParticipantInfo;
import com.hfing.chatservice.exception.ChatServiceException;
import com.hfing.chatservice.exception.ErrorCode;
import com.hfing.chatservice.mapper.ConversationMapper;
import com.hfing.chatservice.repository.ConversationRepository;
import com.hfing.chatservice.repository.httpclient.ProfileClient;
import com.hfing.chatservice.service.ConversationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "CHAT-SERVICE")
public class ConversationServiceImpl implements ConversationService {

    private final ConversationRepository conversationRepository;
    private final ProfileClient profileClient;
    private final ConversationMapper conversationMapper;

    @Override
    public List<ConversationResponse> myConversations() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null)
            throw new ChatServiceException(ErrorCode.UNAUTHORIZED);

        String userId = authentication.getName();

        List<Conversation> conversations = conversationRepository.findAllByParticipantIdsContains(userId);


        return conversations.stream().map(this::toConversationResponse).toList();
    }

    @Override
    public ConversationResponse createConversation(ConversationRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null)
            throw new ChatServiceException(ErrorCode.UNAUTHORIZED);

        String userId = authentication.getName();

        var userProfileResponse = profileClient.getProfile(userId);
        var participantInfoResponse = profileClient.getProfile(request.participantIds().getFirst());
        if(Objects.isNull(userProfileResponse == null || Objects.isNull(participantInfoResponse == null )))
            throw new ChatServiceException(ErrorCode.USER_NOT_FOUND);

        log.info("participantId: {}", participantInfoResponse.data());
        var userProfile = userProfileResponse.data();
        var participantProfile = participantInfoResponse.data();

        List<String> userIds = new ArrayList<>();


        userIds.add(userId);
        userIds.add(participantProfile.id());

        var sortedIds = userIds.stream().sorted().toList();
        String userIdHash = generateParticipantHash(sortedIds);

        List<ParticipantInfo> participants = List.of(
                ParticipantInfo.builder()
                        .userId(userProfile.id())
                        .lastName(userProfile.lastName())
                        .firstName(userProfile.firstName())
                        .avatar(userProfile.avatarKey())
                        .build(),
                ParticipantInfo.builder()
                        .userId(participantProfile.id())
                        .lastName(participantProfile.lastName())
                        .firstName(participantProfile.firstName())
                        .avatar(participantProfile.avatarKey())
                        .build()
        );

        // build conversation
        Conversation conversation = Conversation.builder()
                .type(request.type())
                .participantsHash(userIdHash)
                .participants(participants)
                .createdAt(Instant.now())
                .modifiedDate(Instant.now())
                .build();

        conversation = conversationRepository.save(conversation);


        return toConversationResponse(conversation) ;
    }


    private String generateParticipantHash(List<String> ids) {
        StringJoiner stringJoiner = new StringJoiner("_");
        ids.forEach(stringJoiner::add);
        return stringJoiner.toString();
    }


    private ConversationResponse toConversationResponse(Conversation conversation) {
        String currentUserId = SecurityContextHolder.getContext().getAuthentication().getName();

        ConversationResponse base = conversationMapper.toConversationResponse(conversation);

        var otherParticipant = conversation.getParticipants().stream()
                .filter(p -> !p.getUserId().equals(currentUserId))
                .findFirst()
                .orElse(null);

        return ConversationResponse.builder()
                .id(base.id())
                .type(base.type())
                .participantsHash(base.participantsHash())
                .conversationAvatar(otherParticipant != null ? otherParticipant.getAvatar() : null)
                .conversationName(otherParticipant != null
                        ? otherParticipant.getFirstName() + " " + otherParticipant.getLastName()
                        : null)
                .participants(base.participants())
                .createdDate(base.createdDate())
                .modifiedDate(base.modifiedDate())
                .build();
    }

}
