package com.cse299.skillSphere.controllers;

import com.cse299.skillSphere.dto.CourseResponse;
import com.cse299.skillSphere.services.CategoryService;
import com.cse299.skillSphere.services.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {

    @Value("${minio.url:http://localhost:9000}")
    private String minioUrl;

    @Value("${minio.bucket.name:skillsphere}")
    private String bucketName;

    private final CourseService courseService;
    private final CategoryService categoryService;

    @GetMapping({"/", ""})
    public String index(Model model) {
        return "home";
    }

    @GetMapping("/explore")
    public String explore(Model model) {
        List<CourseResponse> courses = courseService.getAllCoursesForUser();
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("courses", courses);
        model.addAttribute("bucketName", bucketName);
        model.addAttribute("minioUrl", minioUrl);
        return "explore-courses";
    }
}
