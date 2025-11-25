package com.tien.chatservice.mapper;

import org.mapstruct.Mapper;

import com.tien.chatservice.dto.response.ConversationResponse;
import com.tien.chatservice.entity.Conversation;

@Mapper(componentModel = "spring")
public interface ConversationMapper {
    ConversationResponse toConversationResponse(Conversation conversation);
}
