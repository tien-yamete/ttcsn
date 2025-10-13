package com.tien.chatservice.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;
import org.springframework.data.mongodb.core.index.Indexed;
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

    String type; // group, direct

    @Indexed(unique = true)
    String participantsHash;

    List<ParticipantInfo> participants;

    Instant createdDate;

    Instant modifiedDate;
}
