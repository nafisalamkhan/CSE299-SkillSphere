package com.cse299.skillSphere.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class CourseRequest {
    private Integer courseId;
    private String title;
    private String courseDate;
    private Integer categoryId;
    private String level;
    private MultipartFile courseImage;
    private List<SectionRequest> sections;
}
