package com.cse299.skillSphere.controllers;

import com.cse299.skillSphere.dto.CourseResponse;
import com.cse299.skillSphere.services.CategoryService;
import com.cse299.skillSphere.services.CourseService;
import com.cse299.skillSphere.services.EnrollmentService;
import com.cse299.skillSphere.utils.AuthUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user/courses")
public class UserCourseController {

    private static final String COURSES_LIST = "/user-courses/courses";
    private static final String MY_COURSES = "/user-courses/my-courses";
    private static final String COURSE_DETAILS = "/user-courses/course-details";

    private final CourseService courseService;
    private final EnrollmentService enrollmentService;
    private final CategoryService categoryService;
    private final AuthUtils authUtils;

    @GetMapping
    public String userCourseIndex(Model model) {
        // get all courses
        List<CourseResponse> courses = courseService.getAllCoursesForUser();
        model.addAttribute("courses", courses);
        return COURSES_LIST;
    }

    @GetMapping("/my-courses")
    public String myCoursesIndex(Model model) {
        // get enrolled courses for logged-in user
        List<CourseResponse> enrolledCourses = courseService.getEnrolledCourses();
        model.addAttribute("courses", enrolledCourses);
        model.addAttribute("categories", categoryService.getAllCategories());
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
}