package com.hfing.chatservice.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.Instant;
import java.util.List;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "conversation")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Conversation {

    @MongoId
    String id;

    String type; // GROUP, DIRECT

    @Indexed(unique = true)
    String participantsHash;

    List<ParticipantInfo> participants;

    @CreatedDate
    Instant createdAt;

    @LastModifiedDate
    Instant modifiedDate;
}