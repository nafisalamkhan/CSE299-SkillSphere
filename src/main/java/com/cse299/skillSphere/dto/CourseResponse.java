package com.cse299.skillSphere.dto;

import lombok.Data;

import java.util.List;

@Data
public class CourseResponse {
    private int courseId;
    private String title;
    private String courseDate;
    private int totalStudent;
    private String category;
    private String instructor;
    private List<SectionResponse> sections;
}
