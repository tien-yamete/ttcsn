package com.tien.notificationservice.controller;

import com.tien.notificationservice.dto.ApiResponse;
import com.tien.notificationservice.dto.PageResponse;
import com.tien.notificationservice.dto.response.NotificationResponse;
import com.tien.notificationservice.service.NotificationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserNotificationController {
	NotificationService notificationService;

	@GetMapping
	ApiResponse<PageResponse<NotificationResponse>> getMyNotifications(
			@RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "20") int size) {
		return ApiResponse.<PageResponse<NotificationResponse>>builder()
				.result(notificationService.getMyNotifications(page, size))
				.build();
	}

	@PutMapping("/{id}/read")
	ApiResponse<NotificationResponse> markAsRead(@PathVariable String id) {
		return ApiResponse.<NotificationResponse>builder()
				.result(notificationService.markAsRead(id))
				.build();
	}

	@PutMapping("/read-all")
	ApiResponse<Void> markAllAsRead() {
		notificationService.markAllAsRead();
		return ApiResponse.<Void>builder()
				.message("Đã đánh dấu tất cả notifications là đã đọc")
				.build();
	}

	@GetMapping("/unread-count")
	ApiResponse<Long> getUnreadCount() {
		return ApiResponse.<Long>builder()
				.result(notificationService.getUnreadCount())
				.build();
	}
}

