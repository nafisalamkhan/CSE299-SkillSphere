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
public class UserCourseProgressResponse {
    private Integer totalVideos;
    private Integer watchedVideos;
    private Integer progressPercentage;
    private List<Integer> watchedVideoIds;
    private String lastAccessedDate;
}