package com.cse299.skillSphere.services;

import com.cse299.skillSphere.dto.*;
import com.cse299.skillSphere.dto.CourseRequest;
import com.cse299.skillSphere.models.*;
import com.cse299.skillSphere.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;
    private final SectionRepository sectionRepository;
    private final VideoRepository videoRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final MinIOService minIOService;

    @Transactional(rollbackFor = Exception.class)
    public Course createCourse(CourseRequest request) {
        var principal = (org.springframework.security.core.userdetails.User)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User instructor = userRepository.findByUsername(principal.getUsername()).orElseThrow();

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

    @Transactional(rollbackFor = {Exception.class})
    public void createCourseToMinIO(CourseRequest request) {
        var principal = (org.springframework.security.core.userdetails.User)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User instructor = userRepository.findByUsername(principal.getUsername()).orElseThrow();

        Category category = categoryRepository.findById(request.getCategoryId()).orElseThrow();

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
        var principal = (org.springframework.security.core.userdetails.User)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User instructor = userRepository.findByUsername(principal.getUsername())
                .orElseThrow();
        return courseRepository.findAllByInstructorId(instructor.getId()).stream()
                .map(this::getCourseResponse)
                .toList();
    }

    private CourseResponse getCourseResponse(Course c) {
        CourseResponse response = new CourseResponse();
        response.setCourseId(c.getCourseId());
        response.setTitle(c.getTitle());
        response.setCategory(c.getCategory().getName());
        response.setInstructor(c.getInstructor().getName());
        response.setCourseDate(c.getCourseDate());
        response.setTotalStudent(c.getStudents().size());

        List<SectionResponse> sectionResponses = sectionRepository.findAllByCourseCourseId(c.getCourseId()).stream()
                .map(s -> {
                    SectionResponse sectionResponse = new SectionResponse();
                    sectionResponse.setTitle(s.getTitle());
                    sectionResponse.setDescription(s.getDescription());

                    List<VideoResponse> videos = videoRepository.findAllBySectionId(s.getId()).stream()
                            .map(v -> {
                                VideoResponse videoResponse = new VideoResponse();
                                videoResponse.setTitle(v.getTitle());
                                videoResponse.setDescription(v.getDescription());
                                videoResponse.setFileName(v.getFileName());
                                videoResponse.setFileType(v.getFileType());
                                videoResponse.setBase64(v.getVideoBase64());
                                videoResponse.setFilePath(v.getFilePath());
                                return videoResponse;
                            }).toList();

                    sectionResponse.setVideos(videos);
                    return sectionResponse;
                }).toList();

        response.setSections(sectionResponses);
        return response;
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
}
