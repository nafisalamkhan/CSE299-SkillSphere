package com.cse299.skillSphere.controllers;

import com.cse299.skillSphere.dto.CourseRequest;
import com.cse299.skillSphere.services.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/courses")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @GetMapping("/create")
    public String showCreateCourseForm(Model model) {
        model.addAttribute("courseRequest", new CourseRequest());
        return "/courses/create-course";
    }

    @PostMapping("/create")
    public String createCourse(@ModelAttribute CourseRequest request) {
        courseService.createCourseToMinIO(request);
        return "redirect:/courses";
    }

    @GetMapping
    public String courses(Model model) {
        model.addAttribute("courses", courseService.getCoursesForLoggedInInstructor());
        return "/courses/courses";
    }
}
