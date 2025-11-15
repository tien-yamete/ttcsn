package com.tien.fileservice.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageVersions {
    private String thumbnail;
    private String medium;
    private String large;
    private String original;
}
