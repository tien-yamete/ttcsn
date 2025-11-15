package com.tien.fileservice.dto.response;

import java.time.Instant;

import com.tien.sharedcontacts.media.entity.ImageType;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ImageResponse {
    String secureUrl;
    String publicId;
    String url;
    String thumbnailUrl;

    Instant width;
    Instant height;
    String format;
    Long size;
    String contentType;

    String ownerId;
    String postId;
    ImageType imageType;

    Instant createdDate;
}
