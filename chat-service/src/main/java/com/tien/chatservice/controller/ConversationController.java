package com.tien.chatservice.controller;

import com.tien.chatservice.dto.ApiResponse;
import com.tien.chatservice.dto.request.AddParticipantRequest;
import com.tien.chatservice.dto.request.ConversationRequest;
import com.tien.chatservice.dto.request.UpdateConversationRequest;
import com.tien.chatservice.dto.response.ConversationResponse;
import com.tien.chatservice.service.ConversationService;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("conversations")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ConversationController {
    ConversationService conversationService;

    @PostMapping("/create")
    ApiResponse<ConversationResponse> createConversation(@RequestBody @Valid ConversationRequest request) {
        return ApiResponse.<ConversationResponse>builder()
                .result(conversationService.create(request))
                .build();
    }

    @GetMapping("/my-conversations")
    ApiResponse<List<ConversationResponse>> myConversations() {
        return ApiResponse.<List<ConversationResponse>>builder()
                .result(conversationService.myConversations())
                .build();
    }

    @GetMapping("/{id}")
    ApiResponse<ConversationResponse> getById(@PathVariable String id) {
        return ApiResponse.<ConversationResponse>builder()
                .result(conversationService.getById(id))
                .build();
    }

    @PutMapping("/{id}")
    ApiResponse<ConversationResponse> update(
            @PathVariable String id,
            @RequestBody @Valid UpdateConversationRequest request) {
        return ApiResponse.<ConversationResponse>builder()
                .result(conversationService.update(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    ApiResponse<Void> delete(@PathVariable String id) {
        conversationService.delete(id);
        return ApiResponse.<Void>builder().build();
    }

    @PostMapping("/{id}/participants")
    ApiResponse<ConversationResponse> addParticipants(
            @PathVariable String id,
            @RequestBody @Valid AddParticipantRequest request) {
        return ApiResponse.<ConversationResponse>builder()
                .result(conversationService.addParticipants(id, request))
                .build();
    }

    @DeleteMapping("/{id}/participants/{participantId}")
    ApiResponse<ConversationResponse> removeParticipant(
            @PathVariable String id,
            @PathVariable String participantId) {
        return ApiResponse.<ConversationResponse>builder()
                .result(conversationService.removeParticipant(id, participantId))
                .build();
    }

    @PostMapping("/{id}/leave")
    ApiResponse<Void> leave(@PathVariable String id) {
        conversationService.leave(id);
        return ApiResponse.<Void>builder().build();
    }
}
