package com.cse299.skillSphere.controllers;

import com.cse299.skillSphere.dto.*;
import com.cse299.skillSphere.repositories.UserVideoProgressRepository;
import com.cse299.skillSphere.services.CategoryService;
import com.cse299.skillSphere.services.CourseService;
import com.cse299.skillSphere.services.EnrollmentService;
import com.cse299.skillSphere.utils.AuthUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user/courses")
public class UserCourseController {

    @Value("${minio.url:http://localhost:9000}")
    private String minioUrl;

    @Value("${minio.bucket.name:skillsphere}")
    private String bucketName;

    private static final String COURSES_LIST = "/user-courses/courses";
    private static final String MY_COURSES = "/user-courses/my-courses";
    private static final String COURSE_DETAILS = "/user-courses/course-details";

    private final CourseService courseService;
    private final EnrollmentService enrollmentService;
    private final CategoryService categoryService;
    private final AuthUtils authUtils;
    private final UserVideoProgressRepository progressRepository;

    @GetMapping
    public String userCourseIndex(Model model,
                                  @RequestParam(required = false) List<Integer> categoryIds) {
        // Get filtered courses or all courses if no filter applied
        List<CourseResponse> courses;
        if (categoryIds != null && !categoryIds.isEmpty()) {
            courses = courseService.getCoursesByCategoryIds(categoryIds);
        } else {
            courses = courseService.getAllCoursesForUser();
        }

        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("courses", courses);
        model.addAttribute("bucketName", bucketName);
        model.addAttribute("minioUrl", minioUrl);
        model.addAttribute("selectedCategoryIds", categoryIds != null ? categoryIds : List.of());

        return COURSES_LIST;
    }

    @GetMapping("/my-courses")
    public String myCoursesIndex(Model model, @RequestParam(required = false) Integer categoryId) {
        // Get enrolled courses for logged-in user, optionally filtered by category
        List<CourseResponse> enrolledCourses;

        if (categoryId != null) {
            enrolledCourses = courseService.getEnrolledCoursesByCategory(categoryId);
        } else {
            enrolledCourses = courseService.getEnrolledCourses();
        }

        model.addAttribute("courses", enrolledCourses);
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("bucketName", bucketName);
        model.addAttribute("minioUrl", minioUrl);
        model.addAttribute("selectedCategoryId", categoryId);

        return MY_COURSES;
    }

    @GetMapping("/{courseId}")
    public String getCourseDetails(@PathVariable Integer courseId, Model model) {
        CourseResponse course = courseService.getCourseById(courseId);
        boolean isEnrolled = enrollmentService.isUserEnrolled(courseId);
        boolean isCompleted = isEnrolled && enrollmentService.isCourseCompleted(courseId);

        model.addAttribute("course", course);
        model.addAttribute("isEnrolled", isEnrolled);
        model.addAttribute("isCompleted", isCompleted);
        model.addAttribute("instructorImage", authUtils.getLoggedInUser().getProfileImageFilePath());
        model.addAttribute("bucketName", bucketName);
        model.addAttribute("minioUrl", minioUrl);

        model.addAttribute("userRating", courseService.getUserRating(courseId));

        return COURSE_DETAILS;
    }

    @PostMapping("/{courseId}/enroll")
    public String enrollToCourse(@PathVariable Integer courseId) {
        // enroll logged-in user to course
        enrollmentService.enrollUserToCourse(courseId);
        return "redirect:/user/courses/" + courseId;
    }

    @PostMapping("/{courseId}/complete")
    public String completeCourse(@PathVariable Integer courseId) {
        // mark course as completed for logged-in user
        enrollmentService.markCourseAsCompleted(courseId);
        return "redirect:/user/courses/" + courseId;
    }

    @GetMapping("/{courseId}/content")
    public String getCourseContent(@PathVariable Integer courseId,
                                   @RequestParam(required = false) Integer videoId,
                                   Model model) {
        CourseResponse course = courseService.getCourseById(courseId);

        // Get user's progress for this course
        UserCourseProgressResponse progress = enrollmentService.getUserCourseProgress(courseId);

        if (videoId == null && progress.getWatchedVideoIds() != null && !progress.getWatchedVideoIds().isEmpty()) {
            videoId = course.getSections().stream()
                    .flatMap(section -> section.getVideos().stream())
                    .filter(video -> !progress.getWatchedVideoIds().contains(video.getId()))
                    .min(Comparator.comparing(VideoResponse::getId))
                    .map(VideoResponse::getId)
                    .orElse(null);
        }

        // Get the current video (first video of first section if not specified)
        VideoResponse currentVideo = null;
        int currentSectionIndex = 0;
        int currentVideoIndex = 0;

        if (videoId != null) {
            // Find the video and its indices
            for (int i = 0; i < course.getSections().size(); i++) {
                SectionResponse section = course.getSections().get(i);
                for (int j = 0; j < section.getVideos().size(); j++) {
                    VideoResponse video = section.getVideos().get(j);
                    if (video.getId().equals(videoId)) {
                        currentVideo = video;
                        currentSectionIndex = i;
                        currentVideoIndex = j;
                        break;
                    }
                }
                if (currentVideo != null) break;
            }
        }

        // If video not found or not specified, get the first video
        if (currentVideo == null && !course.getSections().isEmpty() &&
            !course.getSections().getFirst().getVideos().isEmpty()) {
            currentVideo = course.getSections().getFirst().getVideos().getFirst();
        }

        // Get navigation info (next/previous videos)
        VideoNavigationInfo navInfo = getNavigationInfo(course, currentSectionIndex, currentVideoIndex);

        model.addAttribute("course", course);
        model.addAttribute("currentVideo", currentVideo);
        model.addAttribute("currentSectionIndex", currentSectionIndex);
        model.addAttribute("currentVideoIndex", currentVideoIndex);
        model.addAttribute("progress", progress);
        model.addAttribute("navInfo", navInfo);
        model.addAttribute("bucketName", bucketName);
        model.addAttribute("minioUrl", minioUrl);

        return "/user-courses/course-content";
    }

    private VideoNavigationInfo getNavigationInfo(CourseResponse course, int sectionIndex, int videoIndex) {
        VideoNavigationInfo info = new VideoNavigationInfo();

        List<SectionResponse> sections = course.getSections();
        if (sections.isEmpty()) return info;

        SectionResponse currentSection = sections.get(sectionIndex);
        List<VideoResponse> videos = currentSection.getVideos();

        // Set previous video info
        if (videoIndex > 0) {
            // Previous video in same section
            info.setPreviousVideoId(videos.get(videoIndex - 1).getId());
            info.setPreviousVideoTitle(videos.get(videoIndex - 1).getTitle());
        } else if (sectionIndex > 0) {
            // Last video of previous section
            SectionResponse prevSection = sections.get(sectionIndex - 1);
            if (!prevSection.getVideos().isEmpty()) {
                VideoResponse prevVideo = prevSection.getVideos().get(prevSection.getVideos().size() - 1);
                info.setPreviousVideoId(prevVideo.getId());
                info.setPreviousVideoTitle(prevVideo.getTitle());
            }
        }

        // Set next video info
        if (videoIndex < videos.size() - 1) {
            // Next video in same section
            info.setNextVideoId(videos.get(videoIndex + 1).getId());
            info.setNextVideoTitle(videos.get(videoIndex + 1).getTitle());
        } else if (sectionIndex < sections.size() - 1) {
            // First video of next section
            SectionResponse nextSection = sections.get(sectionIndex + 1);
            if (!nextSection.getVideos().isEmpty()) {
                VideoResponse nextVideo = nextSection.getVideos().get(0);
                info.setNextVideoId(nextVideo.getId());
                info.setNextVideoTitle(nextVideo.getTitle());
            }
        }

        return info;
    }

    @ResponseBody
    @GetMapping("/{courseId}/videos/{videoId}/progress")
    public ResponseEntity<Map<String, Object>> getVideoProgress(
            @PathVariable Integer courseId,
            @PathVariable Integer videoId) {
        Integer userId = authUtils.getLoggedInUser().getId().intValue();

        Map<String, Object> response = new HashMap<>();

        progressRepository.findByUserIdAndCourseIdAndVideoId(userId, courseId, videoId)
                .ifPresent(progress -> {
                    response.put("currentPositionSeconds", progress.getCurrentPositionSeconds());
                    response.put("progressPercentage", progress.getProgressPercentage());
                    response.put("watched", progress.isWatched());
                    response.put("lastWatchedDate", progress.getLastWatchedDate());
                });

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/rate")
    public String rateCourse(@PathVariable("id") Integer courseId,
                             @RequestParam("rating") Integer rating,
                             RedirectAttributes redirectAttributes) {
        if (!enrollmentService.isUserEnrolled(courseId)) {
            redirectAttributes.addFlashAttribute("error", "You must be enrolled in the course to rate it");
            return "redirect:/user/courses/" + courseId;
        }
        courseService.saveOrUpdateRating(courseId, rating);
        redirectAttributes.addAttribute("ratingSuccess", true);

        return "redirect:/user/courses/" + courseId;
    }

}