package com.cse299.skillSphere.repositories;

import com.cse299.skillSphere.dto.CourseResponse;
import com.cse299.skillSphere.models.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Integer> {

    List<Course> findAllByInstructorId(Long id);

    Optional<Course> findByTitle(String title);

    List<Course> findAllByCategoryCategoryIdIn(List<Integer> categoryIds);

    @Query(value = """
            select c from Course c order by (select count(*) from Enrollment en where en.course = c) desc limit :limit
            """)
    List<Course> findPopularCourses(int limit);


    @Query(value = """
            select c from Course c where c.category.categoryId in (:categories) and c.level in (:levels)
            and (select coalesce(avg(cr.rating), 0) from CourseRating cr where cr.course=c) >= :minRating
            """)
    List<Course> findForExploreCourses(List<Integer> categories, Double minRating, List<String> levels, String sortBy);

}
