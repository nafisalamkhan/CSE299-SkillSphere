package com.cse299.skillSphere.controllers;

import com.cse299.skillSphere.dto.UserCourseProgressResponse;
import com.cse299.skillSphere.models.User;
import com.cse299.skillSphere.services.EnrollmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @ResponseBody
    @PostMapping("/{courseId}/videos/{videoId}/watch")
    public ResponseEntity<Map<String, String>> markVideoWatched(
            @PathVariable Integer courseId,
            @PathVariable Integer videoId) {
        enrollmentService.markVideoWatched(courseId, videoId);

        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Video marked as watched");

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{courseId}/videos/{videoId}/unwatch")
    public ResponseEntity<Map<String, String>> markVideoUnwatched(
            @PathVariable Integer courseId,
            @PathVariable Integer videoId) {
        enrollmentService.markVideoUnwatched(courseId, videoId);

        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Video marked as unwatched");

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{courseId}/videos/{videoId}/progress")
    public ResponseEntity<Map<String, String>> updateVideoProgress(
            @PathVariable Integer courseId,
            @PathVariable Integer videoId,
            @RequestParam Integer position,
            @RequestParam Integer duration) {
        enrollmentService.updateVideoProgress(courseId, videoId, position, duration);

        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Video progress updated");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{courseId}/progress")
    public ResponseEntity<UserCourseProgressResponse> getCourseProgress(
            @PathVariable Integer courseId) {
        UserCourseProgressResponse progress = enrollmentService.getUserCourseProgress(courseId);
        return ResponseEntity.ok(progress);
    }

    @PostMapping("/{courseId}/reset-progress")
    public ResponseEntity<Map<String, String>> resetCourseProgress(
            @PathVariable Integer courseId) {
        enrollmentService.resetCourseProgress(courseId);

        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Course progress has been reset");

        return ResponseEntity.ok(response);
    }
}
