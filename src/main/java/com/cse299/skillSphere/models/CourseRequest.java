package com.cse299.skillSphere.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class CourseRequest {
    private String title;
    private String description;
    private Category category;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String availability;
    private boolean isActive;

}
