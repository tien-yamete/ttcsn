package com.tien.chatservice.mapper;

import com.tien.chatservice.dto.request.ChatMessageRequest;
import com.tien.chatservice.dto.response.ChatMessageResponse;
import com.tien.chatservice.entity.ChatMessage;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ChatMessageMapper {
    ChatMessageResponse toChatMessageResponse(ChatMessage chatMessage);
    
    ChatMessage toChatMessage(ChatMessageRequest request);
}
