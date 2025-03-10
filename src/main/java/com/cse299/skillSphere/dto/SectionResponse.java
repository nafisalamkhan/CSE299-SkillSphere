package com.cse299.skillSphere.dto;

import lombok.Data;

import java.util.List;

@Data
public class SectionResponse {
    private String title;
    private String description;
    private List<VideoResponse> videos;
}
