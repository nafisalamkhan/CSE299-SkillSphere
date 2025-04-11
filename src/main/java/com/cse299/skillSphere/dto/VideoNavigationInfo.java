package com.cse299.skillSphere.dto;

import lombok.Data;

@Data
public class VideoNavigationInfo {
    private Integer previousVideoId;
    private String previousVideoTitle;
    private Integer nextVideoId;
    private String nextVideoTitle;
}