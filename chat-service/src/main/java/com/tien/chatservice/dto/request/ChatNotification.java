package com.tien.chatservice.dto.request;

import com.tien.chatservice.constant.ChatNotificationType;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChatNotification {
    String conversationId;
    String sender;
    String content;
    ChatNotificationType type;
}
