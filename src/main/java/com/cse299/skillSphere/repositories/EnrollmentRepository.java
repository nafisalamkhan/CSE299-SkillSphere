package com.cse299.skillSphere.repositories;

import com.cse299.skillSphere.models.Course;
import com.cse299.skillSphere.models.Enrollment;
import com.cse299.skillSphere.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Integer> {
    boolean existsByStudentAndCourse(User student, Course course);  // check if a student is already enrollerd
    List<Enrollment> findByCourse(Course course); // find all the enrollments for a course
    Enrollment findByStudentAndCourse(User student, Course course); //find enrollments by student and course
}
