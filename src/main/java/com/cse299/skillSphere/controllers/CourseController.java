package com.cse299.skillSphere.controllers;

import com.cse299.skillSphere.dto.CourseRequest;
import com.cse299.skillSphere.dto.CourseResponse;
import com.cse299.skillSphere.models.Category;
import com.cse299.skillSphere.services.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/courses")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @GetMapping({"/create", "/create/"})
    public String showCreateCourseForm(Model model) {
        model.addAttribute("courseRequest", new CourseRequest());

        // Add categories and instructors for dropdowns
        List<Category> categories = courseService.getAllCategories();
        model.addAttribute("categories", categories);

        return "/courses/create-course";
    }

    @PostMapping("/create")
    public String createCourse(@ModelAttribute CourseRequest request) {
        System.out.println("creating course: " + request);
        courseService.createCourseToMinIO(request);
        return "redirect:/courses";
    }

    @GetMapping
    public String courses(Model model) {
        List<CourseResponse> courses = courseService.getCoursesForLoggedInInstructor();
        model.addAttribute("courses", courses);
        model.addAttribute("totalStudents", courses.stream().mapToInt(CourseResponse::getTotalStudent).sum());
        model.addAttribute("totalSections", courses.stream().mapToInt(it -> it.getSections().size()).sum());

        int totalVideos = courses.stream()
                .flatMap(course -> course.getSections().stream())
                .flatMap(section -> section.getVideos().stream())
                .mapToInt(v -> 1)
                .sum();

        model.addAttribute("totalVideos", totalVideos);
        return "/courses/courses";
    }

}
