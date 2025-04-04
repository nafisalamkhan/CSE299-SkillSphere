package com.cse299.skillSphere.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class VideoRequest {
    private Integer id;
    private String title;
    private String description;
    private String filePath; // for preview while editing
    private MultipartFile file;
}
