package com.tien.chatservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.tien.chatservice.entity.ReadReceipt;

@Repository
public interface ReadReceiptRepository extends MongoRepository<ReadReceipt, String> {
    Optional<ReadReceipt> findByMessageIdAndUserId(String messageId, String userId);

    List<ReadReceipt> findAllByMessageId(String messageId);

    List<ReadReceipt> findAllByConversationIdAndUserId(String conversationId, String userId);
}
