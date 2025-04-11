package com.cse299.skillSphere.services;

import com.cse299.skillSphere.dto.CourseResponse;
import com.cse299.skillSphere.dto.SectionResponse;
import com.cse299.skillSphere.dto.UserCourseProgressResponse;
import com.cse299.skillSphere.models.*;
import com.cse299.skillSphere.repositories.CourseRepository;
import com.cse299.skillSphere.repositories.EnrollmentRepository;
import com.cse299.skillSphere.repositories.UserRepository;
import com.cse299.skillSphere.repositories.UserVideoProgressRepository;
import com.cse299.skillSphere.utils.AuthUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EnrollmentService {
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final AuthUtils authUtils;
    private final UserVideoProgressRepository progressRepository;
    private final Mapper mapper;

    //enroll a student in a course
    public String enrollStudent(String studentUsername, String courseName) {
        User student = userRepository.findByUsername(studentUsername).orElse(null);
        if (student == null) return "User not found!";


        Set<Role> roles = student.getRoles();    //check if the user is a student
        if (roles.stream().noneMatch(role -> role.getName().equalsIgnoreCase("STUDENT"))) {
            return "Only students can enroll!";
        }

        Course course = courseRepository.findByTitle(courseName).orElse(null);
        if (course == null) return "Course not found!";

        if (course.getStudents().contains(student)) {
            return "You are already enrolled in this course!";
        }

        course.getStudents().add(student);  // Add student to course's list
        courseRepository.save(course);
        return "Enrollment successful!";
    }

    //view students in a course
    public List<User> getEnrolledStudents(String instructorUsername, String courseName) {
        User instructor = userRepository.findByUsername(instructorUsername)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        //check if user is an instructor
        if (instructor.getRoles().stream().noneMatch(role -> role.getName().equalsIgnoreCase("INSTRUCTOR"))) {
            throw new RuntimeException("Only instructors can view students!");
        }

        Course course = courseRepository.findByTitle(courseName)
                .orElseThrow(() -> new RuntimeException("Course not found!"));

        return List.copyOf(course.getStudents()); //set to list of user
    }


    //remove a student from a course
    public String removeStudent(String instructorUsername, String studentUsername, String courseName) {
        User instructor = userRepository.findByUsername(instructorUsername).orElse(null);
        if (instructor == null) return "Instructor not found!";

        Set<Role> roles = instructor.getRoles();
        if (roles.stream().noneMatch(role -> role.getName().equalsIgnoreCase("INSTRUCTOR"))) {
            return "Only instructors can remove students!";
        }

        Course course = courseRepository.findByTitle(courseName).orElse(null);
        if (course == null) return "Course not found!";

        User student = userRepository.findByUsername(studentUsername).orElse(null);
        if (student == null) return "Student not found!";

        if (!course.getStudents().contains(student)) {
            return "Student is not enrolled in this course!";
        }
        course.getStudents().remove(student);
        courseRepository.save(course);
        return "Student removed successfully!";
    }

    public boolean isUserEnrolled(Integer courseId) {
        return enrollmentRepository.existsByStudentIdAndCourseCourseId(authUtils.getLoggedInUser().getId(), courseId);
    }

    public boolean isCourseCompleted(Integer courseId) {
        return enrollmentRepository.isCourseCompleted(authUtils.getLoggedInUser().getId(), courseId);
    }

    public void enrollUserToCourse(Integer courseId) {
        User student = authUtils.getLoggedInUser();
        if (!enrollmentRepository.existsByStudentIdAndCourseCourseId(student.getId(), courseId)) {
            Enrollment enrollment = new Enrollment();
            enrollment.setStudent(student);
            enrollment.setCourse(courseRepository.findById(courseId)
                    .orElseThrow(() -> new RuntimeException("Course not found")));
            enrollment.setIsCompleted(false);
            enrollmentRepository.save(enrollment);
        } else {
            // already enrolled
        }
    }

    public void markCourseAsCompleted(Integer courseId) {
        User student = authUtils.getLoggedInUser();
        if (!enrollmentRepository.existsByStudentIdAndCourseCourseId(student.getId(), courseId)) {
            throw new RuntimeException("You are not enrolled to this course.");
        }
        Enrollment enrollment = enrollmentRepository
                .findByStudentIdAndCourseCourseId(student.getId(), courseId)
                .orElseThrow();
        enrollment.setIsCompleted(true);
        enrollmentRepository.save(enrollment);

    }

    @Transactional
    public UserCourseProgressResponse getUserCourseProgress(Integer courseId) {
        // Get logged in user
        Integer userId = authUtils.getLoggedInUser().getId().intValue();

        // Get course details to determine total videos
        CourseResponse course = courseRepository.findById(courseId).map(mapper::mapToCourseResponse).orElseThrow();
        int totalVideos = 0;

        // Count total videos in the course
        for (SectionResponse section : course.getSections()) {
            totalVideos += section.getVideos().size();
        }

        // Get all watched videos for this user and course
        List<UserVideoProgress> progressList = progressRepository.findByUserIdAndCourseId(userId, courseId);

        // Count watched videos (where watched=true)
        long watchedVideos = progressList.stream().filter(UserVideoProgress::isWatched).count();

        // Calculate progress percentage
        int progressPercentage = totalVideos > 0 ? (int) ((watchedVideos * 100) / totalVideos) : 0;

        // Get list of watched video IDs
        List<Integer> watchedVideoIds = progressList.stream()
                .filter(UserVideoProgress::isWatched)
                .map(UserVideoProgress::getVideoId)
                .collect(Collectors.toList());

        // Get last accessed date
        String lastAccessedDate = progressList.stream()
                .map(UserVideoProgress::getLastWatchedDate)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .map(date -> date.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")))
                .orElse(null);

        // Build and return the response
        return UserCourseProgressResponse.builder()
                .totalVideos(totalVideos)
                .watchedVideos((int) watchedVideos)
                .progressPercentage(progressPercentage)
                .watchedVideoIds(watchedVideoIds)
                .lastAccessedDate(lastAccessedDate)
                .build();
    }

    @Transactional
    public void markVideoWatched(Integer courseId, Integer videoId) {
        // Verify user is enrolled in the course
        if (!isUserEnrolled(courseId)) {
            throw new RuntimeException("You must be enrolled in this course to track progress");
        }

        Integer userId = authUtils.getLoggedInUser().getId().intValue();

        // Find existing progress or create new
        UserVideoProgress progress = progressRepository
                .findByUserIdAndCourseIdAndVideoId(userId, courseId, videoId)
                .orElse(UserVideoProgress.builder()
                        .userId(userId)
                        .courseId(courseId)
                        .videoId(videoId)
                        .progressPercentage(0)
                        .currentPositionSeconds(0)
                        .watched(false)
                        .build());

        // Update watched status and timestamp
        progress.setWatched(true);
        progress.setProgressPercentage(100);
        progress.setLastWatchedDate(LocalDateTime.now());

        // Save progress
        progressRepository.save(progress);

        // If all videos are watched, mark course as completed
        updateCourseCompletionStatus(courseId);
    }

    @Transactional
    public void markVideoUnwatched(Integer courseId, Integer videoId) {
        // Verify user is enrolled in the course
        if (!isUserEnrolled(courseId)) {
            throw new RuntimeException("You must be enrolled in this course to track progress");
        }

        Integer userId = authUtils.getLoggedInUser().getId().intValue();

        // Find existing progress
        progressRepository.findByUserIdAndCourseIdAndVideoId(userId, courseId, videoId)
                .ifPresent(progress -> {
                    progress.setWatched(false);
                    progress.setProgressPercentage(0);
                    progressRepository.save(progress);

                    // Update course completion status
                    updateCourseCompletionStatus(courseId);
                });
    }

    public boolean isVideoWatched(Integer courseId, Integer videoId) {
        Integer userId = authUtils.getLoggedInUser().getId().intValue();

        return progressRepository.findByUserIdAndCourseIdAndVideoId(userId, courseId, videoId)
                .map(UserVideoProgress::isWatched)
                .orElse(false);
    }

    @Transactional
    public void updateVideoProgress(Integer courseId, Integer videoId, Integer positionSeconds, Integer totalSeconds) {
        // Verify user is enrolled in the course
        if (!isUserEnrolled(courseId)) {
            throw new RuntimeException("You must be enrolled in this course to track progress");
        }

        Integer userId = authUtils.getLoggedInUser().getId().intValue();

        // Calculate progress percentage
        int progressPercentage = totalSeconds > 0 ? (positionSeconds * 100) / totalSeconds : 0;

        // Find existing progress or create new
        UserVideoProgress progress = progressRepository
                .findByUserIdAndCourseIdAndVideoId(userId, courseId, videoId)
                .orElse(UserVideoProgress.builder()
                        .userId(userId)
                        .courseId(courseId)
                        .videoId(videoId)
                        .watched(false)
                        .build());

        // Update progress
        progress.setCurrentPositionSeconds(positionSeconds);
        progress.setProgressPercentage(progressPercentage);
        progress.setLastWatchedDate(LocalDateTime.now());

        // Mark as watched if progress is over 90%
        if (progressPercentage >= 90) {
            progress.setWatched(true);
            progress.setProgressPercentage(100);
        }

        // Save progress
        progressRepository.save(progress);

        // If all videos are watched, mark course as completed
        if (progressPercentage >= 90) {
            updateCourseCompletionStatus(courseId);
        }
    }

    @Transactional
    public void resetCourseProgress(Integer courseId) {
        // Verify user is enrolled in the course
        if (!isUserEnrolled(courseId)) {
            throw new RuntimeException("You must be enrolled in this course to reset progress");
        }

        Integer userId = authUtils.getLoggedInUser().getId().intValue();

        // Delete all progress records for this user and course
        progressRepository.deleteAllByUserIdAndCourseId(userId, courseId);

        // Update course completion status
        Enrollment enrollment = enrollmentRepository.findByStudentIdAndCourseCourseId(userId.longValue(), courseId)
                .orElseThrow(() -> new RuntimeException("Enrollment not found"));

        enrollment.setIsCompleted(false);
        enrollment.setCompletionDate(null);
        enrollmentRepository.save(enrollment);
    }

    /**
     * Helper method to update course completion status based on video progress
     */
    private void updateCourseCompletionStatus(Integer courseId) {
        Integer userId = authUtils.getLoggedInUser().getId().intValue();

        // Get course to determine total videos
        CourseResponse course = courseRepository.findById(courseId).map(mapper::mapToCourseResponse).orElseThrow();
        int totalVideos = 0;

        // Count total videos in the course
        for (SectionResponse section : course.getSections()) {
            totalVideos += section.getVideos().size();
        }

        // Count watched videos
        long watchedVideos = progressRepository.countByUserIdAndCourseIdAndWatched(userId, courseId, true);

        // Get enrollment
        Enrollment enrollment = enrollmentRepository.findByStudentIdAndCourseCourseId(userId.longValue(), courseId)
                .orElseThrow(() -> new RuntimeException("Enrollment not found"));

        // Update completion status if all videos are watched
        if (watchedVideos == totalVideos && totalVideos > 0) {
            enrollment.setIsCompleted(true);
            enrollment.setCompletionDate(LocalDateTime.now());
        } else {
            enrollment.setIsCompleted(false);
            enrollment.setCompletionDate(null);
        }

        enrollmentRepository.save(enrollment);
    }

}
