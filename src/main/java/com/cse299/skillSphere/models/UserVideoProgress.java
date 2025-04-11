package com.cse299.skillSphere.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_video_progress",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "course_id", "video_id"})
        }
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserVideoProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "course_id", nullable = false)
    private Integer courseId;

    @Column(name = "video_id", nullable = false)
    private Integer videoId;

    @Column(name = "watched", nullable = false)
    private boolean watched;

    @Column(name = "progress_percentage")
    private Integer progressPercentage;

    @Column(name = "current_position_seconds")
    private Integer currentPositionSeconds;

    @Column(name = "last_watched_date")
    private LocalDateTime lastWatchedDate;
}