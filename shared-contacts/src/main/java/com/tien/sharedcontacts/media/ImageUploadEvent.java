package com.tien.sharedcontacts.media;

import com.tien.sharedcontacts.media.entity.ImageType;

import java.util.List;
import java.util.Map;

public record ImageUploadEvent (
        List<String> files, // Base64 encoded files
        ImageType imageType,
        String ownerId,
        String postId,
        Map<String, Object>[] propertiesMap
) {
}
