package com.tien.chatservice.repository;

import com.tien.chatservice.entity.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {
    List<ChatMessage> findAllByConversationIdOrderByCreatedDateDesc(String conversationId);

    Page<ChatMessage> findAllByConversationIdOrderByCreatedDateDesc(String conversationId, Pageable pageable);

    Optional<ChatMessage> findByIdAndConversationId(String id, String conversationId);
}

