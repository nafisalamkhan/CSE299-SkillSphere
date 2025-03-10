package com.cse299.skillSphere.dto;

import lombok.Data;

import java.util.List;

@Data
public class CourseRequest {
    private String title;
    private String courseDate;
    private int categoryId;
    private int instructorId;
    private List<SectionRequest> sections;
}
