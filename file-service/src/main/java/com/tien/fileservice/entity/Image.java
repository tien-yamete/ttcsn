package com.tien.fileservice.entity;

import java.time.Instant;

import com.tien.sharedcontacts.media.entity.ImageType;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "file")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Image {
    @MongoId
    String id;

    String ownerId;
    String postId;

    String contentType;
    String format;
    Integer width;
    Integer height;
    Long size;

    ImageType imageType;
    String secureUrl;
    String publicId;
    String forder;

    String version;
    String checksum;

    @CreatedDate
    Instant createdDate;

    @LastModifiedDate
    Instant updatedAt;

    ImageVersions imageVersions;
}
