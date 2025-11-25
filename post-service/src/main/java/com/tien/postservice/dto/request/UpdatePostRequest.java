package com.tien.postservice.dto.request;

import java.util.List;

import com.tien.postservice.entity.PrivacyType;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdatePostRequest {
    String content;
    List<String> imageUrls; // URLs của ảnh đã được upload trước
    PrivacyType privacy;
}
