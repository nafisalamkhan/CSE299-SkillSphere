package com.cse299.skillSphere.repositories;

import com.cse299.skillSphere.models.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Integer> {
}
