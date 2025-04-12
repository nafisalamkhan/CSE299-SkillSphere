package com.cse299.skillSphere.services;

import com.cse299.skillSphere.dto.*;
import com.cse299.skillSphere.dto.CourseRequest;
import com.cse299.skillSphere.models.*;
import com.cse299.skillSphere.repositories.*;
import com.cse299.skillSphere.utils.AuthUtils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.Tuple;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;
    private final SectionRepository sectionRepository;
    private final VideoRepository videoRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final MinIOService minIOService;
    private final EnrollmentRepository enrollmentRepository;
    private final AuthUtils authUtils;
    private final Mapper mapper;
    private final CourseRatingRepository courseRatingRepository;

    @Transactional(rollbackFor = Exception.class)
    public Course createCourse(CourseRequest request) {
        User instructor = getLoggedInUser();
        if (instructor.getRoles().stream()
                .map(Role::getName)
                .noneMatch(rn -> rn.equalsIgnoreCase("ROLE_INSTRUCTOR"))) {
            throw new RuntimeException("You are not allowed to create courses.");
        }

        //TODO: set categoryId from dto
        Category category = categoryRepository.findById(/*request.getCategoryId()*/ 1).orElseThrow();

        Course course = new Course();
        course.setTitle(request.getTitle());
        course.setCourseDate(request.getCourseDate());
        course.setCategory(category);
        course.setInstructor(instructor);

        Course savedCourse = courseRepository.save(course);

        for (SectionRequest sectionRequest : request.getSections()) {
            Section section = new Section();
            section.setTitle(sectionRequest.getTitle());
            section.setDescription(sectionRequest.getDescription());
            section.setCourse(savedCourse);
            Section savedSection = sectionRepository.save(section);

            for (VideoRequest videoRequest : sectionRequest.getVideos()) {
                Video video = new Video();
                video.setTitle(videoRequest.getTitle());
                video.setDescription(videoRequest.getDescription());
                video.setFileName(videoRequest.getFile().getOriginalFilename());
                video.setFileType(videoRequest.getFile().getContentType());

                MultipartFile file = videoRequest.getFile();
                if (file != null && !file.isEmpty()) {
                    byte[] fileBytes;
                    try {
                        fileBytes = file.getBytes();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    String base64 = Base64.getEncoder().encodeToString(fileBytes);
                    video.setVideoBase64(base64);
                }

                video.setSection(savedSection);
                videoRepository.save(video);
            }
        }
        return savedCourse;
    }

    @NotNull
    private User getLoggedInUser() {
        var principal = (org.springframework.security.core.userdetails.User)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.findByUsername(principal.getUsername()).orElseThrow();
    }

    @Transactional(rollbackFor = {Exception.class})
    public void createCourseToMinIO(CourseRequest request) {
        User instructor = getLoggedInUser();
        if (instructor.getRoles().stream()
                .map(Role::getName)
                .noneMatch(rn -> rn.equalsIgnoreCase("ROLE_INSTRUCTOR"))) {
            throw new RuntimeException("You are not allowed to create courses.");
        }

        Category category = categoryRepository.findById(request.getCategoryId()).orElseThrow();

        Course course = new Course();
        course.setTitle(request.getTitle());
        course.setCourseDate(request.getCourseDate());
        course.setCategory(category);
        course.setLevel(request.getLevel());
        course.setInstructor(instructor);

        if (request.getCourseImage() != null && !request.getCourseImage().isEmpty()) {
            course.setCourseImageFilePath(minIOService.uploadFile(request.getCourseImage()));
        }

        Course savedCourse = courseRepository.save(course);

        for (SectionRequest sectionRequest : request.getSections()) {
            Section section = new Section();
            section.setTitle(sectionRequest.getTitle());
            section.setDescription(sectionRequest.getDescription());
            section.setCourse(savedCourse);
            Section savedSection = sectionRepository.save(section);

            for (VideoRequest videoRequest : sectionRequest.getVideos()) {
                Video video = new Video();
                video.setTitle(videoRequest.getTitle());
                video.setDescription(videoRequest.getDescription());
                video.setFileName(videoRequest.getFile().getOriginalFilename());
                video.setFileType(videoRequest.getFile().getContentType());

                MultipartFile file = videoRequest.getFile();
                if (file != null && !file.isEmpty()) {
                    try {
                        String filePath = minIOService.uploadFile(file);
                        video.setFilePath(filePath);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    // video.setVideoBase64(base64);
                }

                video.setSection(savedSection);
                videoRepository.save(video);
            }
        }
    }

    public List<CourseResponse> getCoursesForLoggedInInstructor() {
        User instructor = getLoggedInUser();
        return courseRepository.findAllByInstructorId(instructor.getId()).stream()
                .map(mapper::mapToCourseResponse)
                .toList();
    }

    /**
     * Get course details for editing
     */
    @Transactional(readOnly = true)
    public CourseRequest getCourseForEdit(Integer courseId) {
        // Find course or throw exception
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + courseId));

        User instructor = getLoggedInUser();
        if (!Objects.equals(course.getInstructor().getId(), instructor.getId())) {
            throw new RuntimeException("You are not allowed to edit this course.");
        }

        // Convert to CourseRequest for form
        CourseRequest courseRequest = new CourseRequest();
        courseRequest.setCourseId(course.getCourseId());
        courseRequest.setTitle(course.getTitle());
        courseRequest.setCourseDate(course.getCourseDate());
        courseRequest.setCategoryId(course.getCategory().getCategoryId());
        courseRequest.setLevel(course.getLevel());

        // Add sections
        List<SectionRequest> sectionRequests = new ArrayList<>();
        for (Section section : sectionRepository.findAllByCourseCourseId(course.getCourseId())) {
            SectionRequest sectionRequest = new SectionRequest();
            sectionRequest.setId(section.getId());
            sectionRequest.setTitle(section.getTitle());
            sectionRequest.setDescription(section.getDescription());

            // Add videos
            List<VideoRequest> videoRequests = new ArrayList<>();
            for (Video video : videoRepository.findAllBySectionId(section.getId())) {
                VideoRequest videoRequest = new VideoRequest();
                videoRequest.setId(video.getId());
                videoRequest.setTitle(video.getTitle());
                videoRequest.setDescription(video.getDescription());
                videoRequest.setFilePath(video.getFilePath());

                videoRequests.add(videoRequest);
            }

            sectionRequest.setVideos(videoRequests);
            sectionRequests.add(sectionRequest);
        }

        courseRequest.setSections(sectionRequests);
        return courseRequest;
    }

    /**
     * Update an existing course
     */
    @Transactional(rollbackFor = {Exception.class})
    public void updateCourse(CourseRequest courseRequest) {
        // Find course or throw exception
        Course course = courseRepository.findById(courseRequest.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + courseRequest.getCourseId()));

        User instructor = getLoggedInUser();
        if (!Objects.equals(course.getInstructor().getId(), instructor.getId())) {
            throw new RuntimeException("You are not allowed to edit this course.");
        }

        // Update basic course info
        course.setTitle(courseRequest.getTitle());
        course.setCourseDate(courseRequest.getCourseDate());
        course.setLevel(courseRequest.getLevel());

        // Update category if changed
        if (!Objects.equals(courseRequest.getCategoryId(), course.getCategory().getCategoryId())) {
            Category category = categoryRepository.findById(courseRequest.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found with id: " + courseRequest.getCategoryId()));
            course.setCategory(category);
        }

        // Save course first to ensure it has an ID
        course = courseRepository.save(course);

        // Process each section
        updateCourseSections(course, courseRequest.getSections());
    }

    /**
     * Helper method to update course sections
     */
    private void updateCourseSections(Course course, List<SectionRequest> sectionRequests) {
        // Create a map of existing sections by ID for quick lookup
        Map<Integer, Section> existingSectionsMap = sectionRepository.findAllByCourseCourseId(course.getCourseId()).stream()
                .collect(Collectors.toMap(Section::getId, section -> section));

        // Keep track of processed section IDs to know which ones to keep
        Set<Integer> processedSectionIds = new HashSet<>();

        // Process each section in the request
        for (int i = 0; i < sectionRequests.size(); i++) {
            SectionRequest sectionRequest = sectionRequests.get(i);
            Section section;

            // Check if it's an existing section or new one
            if (sectionRequest.getId() != null && sectionRequest.getId() > 0 && existingSectionsMap.containsKey(sectionRequest.getId())) {
                // Update existing section
                section = existingSectionsMap.get(sectionRequest.getId());
                section.setTitle(sectionRequest.getTitle());
                section.setDescription(sectionRequest.getDescription());
                // section.setOrderIndex(i);
            } else {
                // Create new section
                section = new Section();
                section.setCourse(course);
                section.setTitle(sectionRequest.getTitle());
                section.setDescription(sectionRequest.getDescription());
                // section.setOrderIndex(i);
            }

            // Save the section first to ensure it has an ID before handling videos
            section = sectionRepository.saveAndFlush(section);

            // Add this section's ID to the processed set
            processedSectionIds.add(section.getId());

            // Process videos for this section
            updateSectionVideos(section, sectionRequest.getVideos());
        }

        // Find and delete sections that were not processed (meaning they're no longer in the request)
        List<Section> sectionsToDelete = sectionRepository.findAllByCourseCourseId(course.getCourseId()).stream()
                .filter(section -> !processedSectionIds.contains(section.getId()))
                .collect(Collectors.toList());

        if (!sectionsToDelete.isEmpty()) {
            for (Section s : sectionsToDelete) {
                videoRepository.deleteAll(videoRepository.findAllBySectionId(s.getId()));
            }
            sectionRepository.deleteAll(sectionsToDelete);
        }
    }

    /**
     * Helper method to update section videos
     */
    private void updateSectionVideos(Section section, List<VideoRequest> videoRequests) {
        // Make sure the section is already saved and has an ID
        if (section.getId() == 0) {
            throw new IllegalStateException("Section must be saved before adding videos");
        }

        // Create a map of existing videos by ID for quick lookup
        Map<Integer, Video> existingVideosMap = videoRepository.findAllBySectionId(section.getId()).stream()
                .collect(Collectors.toMap(Video::getId, video -> video));

        // Keep track of processed video IDs to know which ones to keep
        Set<Integer> processedVideoIds = new HashSet<>();

        // Process each video in the request
        for (int i = 0; i < videoRequests.size(); i++) {
            VideoRequest videoRequest = videoRequests.get(i);
            Video video;

            // Check if it's an existing video or new one
            if (videoRequest.getId() != null && videoRequest.getId() > 0 && existingVideosMap.containsKey(videoRequest.getId())) {
                // Update existing video
                video = existingVideosMap.get(videoRequest.getId());
                video.setTitle(videoRequest.getTitle());
                video.setDescription(videoRequest.getDescription());
                // video.setOrderIndex(i);

                // Upload new video file if provided
                if (videoRequest.getFile() != null && !videoRequest.getFile().isEmpty()) {
                    // Remove old video file from MinIO
                    minIOService.deleteFile(video.getFilePath());
                    video.setFilePath(minIOService.uploadFile(videoRequest.getFile()));
                }
            } else {
                // Create new video
                video = new Video();
                video.setSection(section);
                video.setTitle(videoRequest.getTitle());
                video.setDescription(videoRequest.getDescription());
                // video.setOrderIndex(i);

                // Upload new video file
                if (videoRequest.getFile() != null && !videoRequest.getFile().isEmpty()) {
                    String filePath = minIOService.uploadFile(videoRequest.getFile());
                    video.setFilePath(filePath);
                } else {
                    throw new IllegalArgumentException("Video file is required for new videos");
                }
            }

            // Save the video and add its ID to processed set
            video = videoRepository.save(video);
            processedVideoIds.add(video.getId());
        }

        // Find and delete videos that were not processed (meaning they're no longer in the request)
        List<Video> videosToDelete = videoRepository.findAllBySectionId(section.getId()).stream()
                .filter(video -> !processedVideoIds.contains(video.getId()))
                .collect(Collectors.toList());

        if (!videosToDelete.isEmpty()) {
            // Delete video files from MinIO
            for (Video video : videosToDelete) {
                minIOService.deleteFile(video.getFilePath());
            }

            videoRepository.deleteAll(videosToDelete);
        }
    }

    public List<CourseResponse> getAllCoursesForUser() {
        return courseRepository.findAll().stream()
                .map(mapper::mapToCourseResponse)
                .toList();
    }

    public CourseResponse getCourseById(Integer courseId) {
        return courseRepository
                .findById(courseId)
                .map(mapper::mapToCourseResponse)
                .orElseThrow(() -> new RuntimeException("Course not found!"));
    }

    public List<CourseResponse> getEnrolledCourses() {
        return enrollmentRepository
                .findAllCoursesByStudentId(authUtils.getLoggedInUser().getId()).stream()
                .map(mapper::mapToCourseResponse).toList();
    }

    public List<CourseResponse> getCoursesByCategoryIds(List<Integer> categoryIds) {
        return courseRepository.findAllByCategoryCategoryIdIn(categoryIds).stream()
                .map(mapper::mapToCourseResponse)
                .toList();
    }

    public List<CourseResponse> getEnrolledCoursesByCategory(Integer categoryId) {
        return enrollmentRepository
                .findAllCoursesByStudentIdAndCategoryId(authUtils.getLoggedInUser().getId(), categoryId).stream()
                .map(mapper::mapToCourseResponse)
                .toList();
    }

    public List<CourseResponse> getPopularCourses(int limit) {
        return courseRepository.findPopularCourses(limit).stream().map(mapper::mapToCourseResponse).toList();
    }

    public List<CourseResponse> filterCourses(List<Integer> categories, Double minRating, List<String> levels, String sortBy) {
        return courseRepository
                .findForExploreCourses(categories, minRating, levels, sortBy).stream()
                .map(mapper::mapToCourseResponse).toList();
    }

    public Integer getUserRating(Integer courseId) {
        User user = authUtils.getLoggedInUser();
        final Integer value = courseRatingRepository.findRatingByUser(courseId, user.getId());
        return value != null ? value : 0;
    }


    @Transactional
    public CourseRating saveOrUpdateRating(Integer courseId, Integer rating) {
        Long userId = authUtils.getLoggedInUser().getId();
        // Validate rating value
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }

        // Find existing rating if exists
        CourseRating existingRating = courseRatingRepository
                .findByCourseCourseIdAndStudentId(courseId, userId)
                .orElse(null);

        if (existingRating != null) {
            // Update existing rating
            existingRating.setRating(rating);
            return courseRatingRepository.save(existingRating);
        } else {
            // Create new rating
            Course course = courseRepository.findById(courseId)
                    .orElseThrow(() -> new EntityNotFoundException("Course not found"));

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("User not found"));

            CourseRating newRating = CourseRating.builder()
                    .course(course)
                    .student(user)
                    .rating(rating)
                    .build();

            CourseRating savedRating = courseRatingRepository.save(newRating);

            return savedRating;
        }
    }


}
