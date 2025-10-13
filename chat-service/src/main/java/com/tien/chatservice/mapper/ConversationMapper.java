package com.tien.chatservice.mapper;

import com.tien.chatservice.dto.response.ConversationResponse;
import com.tien.chatservice.entity.Conversation;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ConversationMapper {
    ConversationResponse toConversationResponse(Conversation conversation);

    List<ConversationResponse> toConversationResponseList(List<Conversation> conversations);
}

