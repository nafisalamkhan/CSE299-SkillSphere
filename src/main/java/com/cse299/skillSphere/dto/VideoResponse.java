package com.cse299.skillSphere.dto;

import lombok.Data;

@Data
public class VideoResponse {
    private String title;
    private String description;
    private String fileName;
    private String fileType;
    private String base64;
    private String filePath;
}
