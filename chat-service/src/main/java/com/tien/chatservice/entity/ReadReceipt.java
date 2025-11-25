package com.tien.chatservice.entity;

import java.time.Instant;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "read_receipt")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReadReceipt {
    @MongoId
    String id;

    @Indexed
    String messageId;

    @Indexed
    String conversationId;

    @Indexed
    String userId;

    Instant readAt;
}
