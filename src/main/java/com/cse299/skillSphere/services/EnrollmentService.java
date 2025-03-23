package com.cse299.skillSphere.services;

import com.cse299.skillSphere.models.Course;
import com.cse299.skillSphere.models.Role;
import com.cse299.skillSphere.models.User;
import com.cse299.skillSphere.repositories.CourseRepository;
import com.cse299.skillSphere.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class EnrollmentService {
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    //enroll a student in a course
    public String enrollStudent(String studentUsername, String courseName) {
        User student = userRepository.findByUsername(studentUsername).orElse(null);
        if (student == null) return "User not found!";


        Set<Role> roles = student.getRoles();    //check if the user is a student
        if (roles.stream().noneMatch(role -> role.getName().equalsIgnoreCase("STUDENT"))) {
            return "Only students can enroll!";
        }

        Course course = courseRepository.findByTitle(courseName).orElse(null);
        if (course == null) return "Course not found!";

        if (course.getStudents().contains(student)) {
            return "You are already enrolled in this course!";
        }

        course.getStudents().add(student);  // Add student to course's list
        courseRepository.save(course);
        return "Enrollment successful!";
    }

    //view students in a course
    public List<User> getEnrolledStudents(String instructorUsername, String courseName) {
        User instructor = userRepository.findByUsername(instructorUsername)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        //check if user is an instructor
        if (instructor.getRoles().stream().noneMatch(role -> role.getName().equalsIgnoreCase("INSTRUCTOR"))) {
            throw new RuntimeException("Only instructors can view students!");
        }

        Course course = courseRepository.findByTitle(courseName)
                .orElseThrow(() -> new RuntimeException("Course not found!"));

        return List.copyOf(course.getStudents()); //set to list of user
    }


    //remove a student from a course
    public String removeStudent(String instructorUsername, String studentUsername, String courseName) {
        User instructor = userRepository.findByUsername(instructorUsername).orElse(null);
        if (instructor == null) return "Instructor not found!";

        Set<Role> roles = instructor.getRoles();
        if (roles.stream().noneMatch(role -> role.getName().equalsIgnoreCase("INSTRUCTOR"))) {
            return "Only instructors can remove students!";
        }

        Course course = courseRepository.findByTitle(courseName).orElse(null);
        if (course == null) return "Course not found!";

        User student = userRepository.findByUsername(studentUsername).orElse(null);
        if (student == null) return "Student not found!";

        if (!course.getStudents().contains(student)) {
            return "Student is not enrolled in this course!";
        }
        course.getStudents().remove(student);
        courseRepository.save(course);
        return "Student removed successfully!";
    }
}
