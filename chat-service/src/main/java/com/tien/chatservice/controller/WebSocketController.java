package com.tien.chatservice.controller;

import com.tien.chatservice.dto.request.ChatMessageRequest;
import com.tien.chatservice.dto.request.ChatNotification;
import com.tien.chatservice.dto.request.TypingNotification;
import com.tien.chatservice.dto.response.ChatMessageResponse;
import com.tien.chatservice.service.ChatMessageService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WebSocketController {

    ChatMessageService chatMessageService;
    SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessageRequest request) {
        log.info("Received message via WebSocket: conversationId={}, message={}", 
                request.getConversationId(), request.getMessage());
        
        ChatMessageResponse response = chatMessageService.create(request);
        
        messagingTemplate.convertAndSend(
                "/topic/conversation/" + request.getConversationId(), 
                response
        );
    }

    @MessageMapping("/chat.typing")
    public void handleTyping(@Payload TypingNotification notification) {
        log.debug("User {} is typing in conversation {}", 
                notification.getUserId(), notification.getConversationId());
        
        messagingTemplate.convertAndSend(
                "/topic/conversation/" + notification.getConversationId() + "/typing",
                notification
        );
    }

    @MessageMapping("/chat.addUser")
    public void handleUserJoin(@Payload ChatNotification notification) {
        log.info("User {} joined conversation {}", 
                notification.getSender(), notification.getConversationId());
        
        messagingTemplate.convertAndSend(
                "/topic/conversation/" + notification.getConversationId(),
                notification
        );
    }
}