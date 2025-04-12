package com.cse299.skillSphere.services;

import com.cse299.skillSphere.dto.CourseResponse;
import com.cse299.skillSphere.dto.SectionResponse;
import com.cse299.skillSphere.dto.VideoResponse;
import com.cse299.skillSphere.models.*;
import com.cse299.skillSphere.repositories.*;
import com.cse299.skillSphere.utils.AuthUtils;
import jakarta.persistence.Tuple;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class Mapper {

    private final SectionRepository sectionRepository;
    private final VideoRepository videoRepository;
    private final AuthUtils authUtils;
    private final UserVideoProgressRepository progressRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final CourseRatingRepository courseRatingRepository;

    public CourseResponse mapToCourseResponse(Course course) {
        long watchedVideos = 0;
        try {
            User user = authUtils.getLoggedInUser();
            List<UserVideoProgress> progressList = progressRepository.findByUserIdAndCourseId(user.getId().intValue(), course.getCourseId());
            // Count watched videos (where watched=true)
            watchedVideos = progressList.stream().filter(UserVideoProgress::isWatched).count();
        } catch (Exception ignored) {
            // for landing page... public course list
        }
        List<SectionResponse> sections = sectionRepository
                .findAllByCourseCourseId(course.getCourseId()).stream()
                .map(this::mapToSectionResponse).toList();

        int students = enrollmentRepository.countAllStudentsByCourseCourseId(course.getCourseId());
        Tuple rating = courseRatingRepository.findAverageRating(course.getCourseId());

        return CourseResponse.builder()
                .courseId(course.getCourseId())
                .courseDate(course.getCourseDate())
                .title(course.getTitle())
                .category(course.getCategory().getName())
                .instructor(course.getInstructor().getName())
                .courseImageFilePath(course.getCourseImageFilePath())
                .sections(sections)
                .totalStudent(students)
                .totalVideos(sections.stream().flatMap(s -> s.getVideos().stream()).mapToInt(v -> 1).sum())
                .level(course.getLevel())
                .rating(rating.get("rating", Double.class))
                .ratingStudentCount(rating.get("students", Integer.class))
                .watchedVideos((int) watchedVideos)
                .build();
    }

    public SectionResponse mapToSectionResponse(Section section) {
        return SectionResponse.builder()
                .title(section.getTitle())
                .description(section.getDescription())
                .videos(videoRepository
                        .findAllBySectionId(section.getId()).stream()
                        .map(this::mapToVideoResponse).toList())
                .build();
    }

    public VideoResponse mapToVideoResponse(Video video) {
        return VideoResponse.builder()
                .id(video.getId())
                .title(video.getTitle())
                .filePath(video.getFilePath())
                .description(video.getDescription())
                .fileName(video.getFileName())
                .fileType(video.getFileType())
                .build();
    }

}
