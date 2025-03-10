package com.cse299.skillSphere.repositories;

import com.cse299.skillSphere.models.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Integer> {

    List<Course> findAllByInstructorId(Long id);
}
