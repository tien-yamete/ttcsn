package com.tien.fileservice.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FileInfo {
    String name;
    String contentType;
    long size;
    String md5Checksum;
    String path;
    String url;
}
