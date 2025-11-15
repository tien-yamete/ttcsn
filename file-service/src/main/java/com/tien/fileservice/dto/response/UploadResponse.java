package com.tien.fileservice.dto.response;

import com.tien.fileservice.entity.ImageVersions;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UploadResponse {
    String publicId;
    Long version;
    Integer width;
    Integer height;

    String secureUrl;
    ImageVersions imageVersions;
}
