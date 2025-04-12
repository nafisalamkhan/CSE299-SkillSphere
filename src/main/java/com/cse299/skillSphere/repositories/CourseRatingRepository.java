package com.cse299.skillSphere.repositories;

import com.cse299.skillSphere.models.CourseRating;
import jakarta.persistence.Tuple;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CourseRatingRepository extends JpaRepository<CourseRating, Integer> {

    @Query(value = """
            select coalesce(avg(cr.rating), 0) as rating, cast(coalesce(count(cr.student), 0) as int) as students
            from CourseRating cr where cr.course.courseId = :courseId
            """)
    Tuple findAverageRating(int courseId);

    @Query(value = """
            select coalesce(cr.rating, 0) from CourseRating cr where cr.course.courseId = :courseId
            and cr.student.id = :userId
            """)
    Integer findRatingByUser(Integer courseId, Long userId);

    Optional<CourseRating> findByCourseCourseIdAndStudentId(Integer courseId, Long userId);
}