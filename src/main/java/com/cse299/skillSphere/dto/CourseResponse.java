package com.cse299.skillSphere.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CourseResponse {
    private Integer courseId;
    private String title;
    private String courseDate;
    private Integer totalStudent;
    private String category;
    private String instructor;
    private String courseImageFilePath;
    private List<SectionResponse> sections;
}
