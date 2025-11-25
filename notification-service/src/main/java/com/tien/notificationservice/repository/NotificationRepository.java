package com.tien.notificationservice.repository;

import com.tien.notificationservice.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NotificationRepository extends MongoRepository<Notification, String> {
	Page<Notification> findByUserIdOrderByCreatedAtDesc(String userId, Pageable pageable);

	long countByUserIdAndIsReadFalse(String userId);

	Optional<Notification> findByIdAndUserId(String id, String userId);
}

