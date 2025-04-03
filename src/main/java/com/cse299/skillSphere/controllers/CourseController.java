package com.cse299.skillSphere.controllers;

import com.cse299.skillSphere.dto.CourseRequest;
import com.cse299.skillSphere.dto.CourseResponse;
import com.cse299.skillSphere.models.Category;
import com.cse299.skillSphere.services.CategoryService;
import com.cse299.skillSphere.services.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/courses")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping({"/create", "/create/"})
    public String showCreateCourseForm(Model model) {
        model.addAttribute("courseRequest", new CourseRequest());

        // Add categories and instructors for dropdowns
        List<Category> categories = categoryService.getAllCategories();
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


    @GetMapping("/edit/{courseId}")
    public String showEditCourseForm(@PathVariable Integer courseId, Model model) {
        try {
            // Get course details for editing
            CourseRequest courseRequest = courseService.getCourseForEdit(courseId);
            model.addAttribute("courseRequest", courseRequest);

            // Add categories for dropdown
            List<Category> categories = categoryService.getAllCategories();
            model.addAttribute("categories", categories);

            return "courses/edit-course";
        } catch (Exception e) {
            // Handle exceptions - course not found, etc.
            return "redirect:/courses?error=Course not found";
        }
    }

    /**
     * Handle course update submission
     */
    @PostMapping("/update")
    public String updateCourse(@ModelAttribute CourseRequest courseRequest,
                               RedirectAttributes redirectAttributes) {
        try {
            // Update the course
            courseService.updateCourse(courseRequest);
            redirectAttributes.addFlashAttribute("success", "Course updated successfully!");
            return "redirect:/courses";
        } catch (Exception e) {
            // Handle exceptions
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Failed to update course: " + e.getMessage());
            return "redirect:/courses/edit/" + courseRequest.getCourseId();
        }
    }
}
