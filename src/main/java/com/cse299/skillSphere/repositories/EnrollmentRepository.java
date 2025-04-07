package com.cse299.skillSphere.repositories;

import com.cse299.skillSphere.models.Course;
import com.cse299.skillSphere.models.Enrollment;
import com.cse299.skillSphere.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Integer> {
    boolean existsByStudentAndCourse(User student, Course course);  // check if a student is already enrollerd

    List<Enrollment> findByCourse(Course course); // find all the enrollments for a course

    Enrollment findByStudentAndCourse(User student, Course course); //find enrollments by student and course

    boolean existsByStudentIdAndCourseCourseId(Long student_id, int course_courseId);

    @Query(value = """
            select count(*) > 0 from Enrollment en where en.course.courseId = :courseId and en.student.id = :userId
            and en.isCompleted = true
            """)
    boolean isCourseCompleted(Long userId, Integer courseId);

    @Query(value = """
            select en.course from Enrollment en where en.student.id = :userId
            """)
    List<Course> findAllCoursesByStudentId(Long userId);

    Optional<Enrollment> findByStudentIdAndCourseCourseId(Long id, Integer courseId);
}
