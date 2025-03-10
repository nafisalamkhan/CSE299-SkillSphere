package com.cse299.skillSphere.repositories;

import com.cse299.skillSphere.models.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SectionRepository extends JpaRepository<Section, Integer> {

    List<Section> findAllByCourseCourseId(int courseId);
}