package com.tien.fileservice.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "file")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FileManagement {
    @MongoId
    String id;
    String ownerId;
    String contentType;
    long size;
    String md5Checksum;
    String path;
}
