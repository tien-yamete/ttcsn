package com.tien.chatservice.dto.request;

import com.tien.chatservice.constant.TypeConversation;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConversationRequest {
    TypeConversation typeConversation;

    @Size(min = 1)
    @NotNull
    List<String> participantIds;
}
