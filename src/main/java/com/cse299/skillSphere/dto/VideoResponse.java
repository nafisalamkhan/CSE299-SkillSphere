package com.cse299.skillSphere.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VideoResponse {
    private Integer id;
    private String title;
    private String description;
    private String fileName;
    private String fileType;
    private String base64;
    private String filePath;
}
