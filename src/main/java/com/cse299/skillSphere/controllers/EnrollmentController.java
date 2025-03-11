package com.cse299.skillSphere.controllers;

import com.cse299.skillSphere.models.Enrollment;
import com.cse299.skillSphere.models.User;
import com.cse299.skillSphere.services.EnrollmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/enrollment")
public class EnrollmentController {

    @Autowired
    private EnrollmentService enrollmentService;

    @PostMapping("/enroll")
    public String enrollStudent(@RequestParam String studentUsername, @RequestParam String courseName, Model model) {
        String result = enrollmentService.enrollStudent(studentUsername, courseName);
        model.addAttribute("message", result);
        return "course_info";
    }

    @GetMapping("/students")
    public String viewEnrolledStudents(@RequestParam String instructorUsername,
                                       @RequestParam String courseName, Model model) {
        try {
            List<User> students = enrollmentService.getEnrolledStudents(instructorUsername, courseName);
            model.addAttribute("students", students);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        return "enrolled-students";
    }

    @PostMapping("/remove")
    public String removeEnrolledStudent(@RequestParam String instructorUsername,
                                        @RequestParam String studentUsername,
                                        @RequestParam String courseName, Model model) {
        String result = enrollmentService.removeStudent(instructorUsername, studentUsername, courseName);
        model.addAttribute("message", result);
        return "enrolled-students";
    }
}
