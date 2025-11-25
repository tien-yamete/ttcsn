package com.tien.notificationservice.service;

import com.tien.notificationservice.dto.PageResponse;
import com.tien.notificationservice.dto.response.NotificationResponse;
import com.tien.notificationservice.entity.Notification;
import com.tien.notificationservice.exception.AppException;
import com.tien.notificationservice.exception.ErrorCode;
import com.tien.notificationservice.mapper.NotificationMapper;
import com.tien.notificationservice.repository.NotificationRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationService {
	NotificationRepository notificationRepository;
	NotificationMapper notificationMapper;
	KafkaTemplate<String, Object> kafkaTemplate;

	private static final String NOTIFICATION_TOPIC = "notification.events";

	public PageResponse<NotificationResponse> getMyNotifications(int page, int size) {
		String userId = getCurrentUserId();

		Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
		Page<Notification> notificationsPage = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId,
				pageable);

		var content = notificationsPage.getContent().stream()
				.map(notificationMapper::toNotificationResponse)
				.collect(Collectors.toList());

		return PageResponse.<NotificationResponse>builder()
				.content(content)
				.page(page)
				.size(size)
				.totalElements(notificationsPage.getTotalElements())
				.totalPages(notificationsPage.getTotalPages())
				.hasNext(notificationsPage.hasNext())
				.hasPrevious(notificationsPage.hasPrevious())
				.build();
	}

	public NotificationResponse markAsRead(String notificationId) {
		String userId = getCurrentUserId();

		Notification notification = notificationRepository.findByIdAndUserId(notificationId, userId)
				.orElseThrow(() -> new AppException(ErrorCode.NOTIFICATION_NOT_FOUND));

		if (!notification.getIsRead()) {
			notification.setIsRead(true);
			notification.setReadAt(Instant.now());
			notification = notificationRepository.save(notification);
		}

		return notificationMapper.toNotificationResponse(notification);
	}

	public void markAllAsRead() {
		String userId = getCurrentUserId();

		var notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId,
				Pageable.unpaged());
		
		var unreadNotifications = notifications.getContent().stream()
				.filter(n -> !n.getIsRead())
				.collect(Collectors.toList());

		if (!unreadNotifications.isEmpty()) {
			Instant now = Instant.now();
			unreadNotifications.forEach(n -> {
				n.setIsRead(true);
				n.setReadAt(now);
			});
			notificationRepository.saveAll(unreadNotifications);
		}
	}

	public Long getUnreadCount() {
		String userId = getCurrentUserId();
		return notificationRepository.countByUserIdAndIsReadFalse(userId);
	}

	public NotificationResponse createNotification(String userId, String type, String title, String content,
			String relatedUserId, String relatedEntityId, String relatedEntityType) {
		Notification notification = Notification.builder()
				.userId(userId)
				.type(type)
				.title(title)
				.content(content)
				.relatedUserId(relatedUserId)
				.relatedEntityId(relatedEntityId)
				.relatedEntityType(relatedEntityType)
				.isRead(false)
				.createdAt(Instant.now())
				.build();

		notification = notificationRepository.save(notification);

		log.info("Created notification {} for user {}", notification.getId(), userId);

		return notificationMapper.toNotificationResponse(notification);
	}

	private String getCurrentUserId() {
		return SecurityContextHolder.getContext().getAuthentication().getName();
	}
}

