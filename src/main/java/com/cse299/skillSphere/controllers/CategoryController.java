package com.cse299.skillSphere.controllers;

import com.cse299.skillSphere.models.Category;
import com.cse299.skillSphere.repositories.UserRepository;
import com.cse299.skillSphere.services.CategoryService;
import com.cse299.skillSphere.services.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/admin-category-create")
    public String showCategoryFrom(Model model) {
        model.addAttribute("category", new Category());
        return "category-create"; //Thymeleaf template
    }

    @PostMapping("/admin-category-create")
    public String createCategory(@ModelAttribute Category category, Model model) {
        categoryService.createCategory(category);
        model.addAttribute("message", "Category created successfully!");
        return "category-create";
    }



}
