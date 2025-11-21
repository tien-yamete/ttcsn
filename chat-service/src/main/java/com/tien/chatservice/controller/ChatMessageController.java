package com.tien.chatservice.controller;

import com.tien.chatservice.dto.ApiResponse;
import com.tien.chatservice.dto.PageResponse;
import com.tien.chatservice.dto.request.ChatMessageRequest;
import com.tien.chatservice.dto.request.UpdateMessageRequest;
import com.tien.chatservice.dto.response.ChatMessageResponse;
import com.tien.chatservice.dto.response.ReadReceiptResponse;
import com.tien.chatservice.service.ChatMessageService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("messages")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ChatMessageController {
    ChatMessageService chatMessageService;

    @PostMapping("/create")
    ApiResponse<ChatMessageResponse> create(
            @RequestBody @Valid ChatMessageRequest request) {
        return ApiResponse.<ChatMessageResponse>builder()
                .result(chatMessageService.create(request))
                .build();
    }

    @GetMapping
    ApiResponse<List<ChatMessageResponse>> getMessages(
            @RequestParam("conversationId") String conversationId) {
        return ApiResponse.<List<ChatMessageResponse>>builder()
                .result(chatMessageService.getMessages(conversationId))
                .build();
    }

    @GetMapping("/paginated")
    ApiResponse<PageResponse<ChatMessageResponse>> getMessagesWithPagination(
            @RequestParam("conversationId") String conversationId,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {
        return ApiResponse.<PageResponse<ChatMessageResponse>>builder()
                .result(chatMessageService.getMessagesWithPagination(conversationId, page, size))
                .build();
    }

    @GetMapping("/{id}")
    ApiResponse<ChatMessageResponse> getById(@PathVariable String id) {
        return ApiResponse.<ChatMessageResponse>builder()
                .result(chatMessageService.getById(id))
                .build();
    }

    @PutMapping("/{id}")
    ApiResponse<ChatMessageResponse> update(
            @PathVariable String id,
            @RequestBody @Valid UpdateMessageRequest request) {
        return ApiResponse.<ChatMessageResponse>builder()
                .result(chatMessageService.update(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    ApiResponse<Void> delete(@PathVariable String id) {
        chatMessageService.delete(id);
        return ApiResponse.<Void>builder().build();
    }

    @PostMapping("/{id}/read")
    ApiResponse<ReadReceiptResponse> markAsRead(@PathVariable String id) {
        return ApiResponse.<ReadReceiptResponse>builder()
                .result(chatMessageService.markAsRead(id))
                .build();
    }

    @GetMapping("/{id}/read-receipts")
    ApiResponse<List<ReadReceiptResponse>> getReadReceipts(
            @PathVariable String id) {
        return ApiResponse.<List<ReadReceiptResponse>>builder()
                .result(chatMessageService.getReadReceipts(id))
                .build();
    }

    @GetMapping("/unread-count")
    ApiResponse<Long> getUnreadCount(@RequestParam("conversationId") String conversationId) {
        return ApiResponse.<Long>builder()
                .result(chatMessageService.getUnreadCount(conversationId))
                .build();
    }
}
