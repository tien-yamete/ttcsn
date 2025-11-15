package com.tien.sharedcommon.converter;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

public class MediaConverter {
    public static List<String> convertToBase64(List<MultipartFile> files) {
        return files.stream()
                .map(file -> {
                    try {
                        return Base64.getEncoder().encodeToString(file.getBytes());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
    }

    public static byte[] decodeFromBase64(String base64) {
        return Base64.getDecoder().decode(base64);
    }
}