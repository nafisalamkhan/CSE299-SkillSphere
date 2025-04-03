package com.cse299.skillSphere.dto;

import lombok.Data;

import java.util.List;

@Data
public class SectionRequest {
    private Integer id;
    private String title;
    private String description;
    private List<VideoRequest> videos;
}
