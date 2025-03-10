package com.cse299.skillSphere.repositories;

import com.cse299.skillSphere.models.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideoRepository extends JpaRepository<Video, Integer> {

    List<Video> findAllBySectionId(int sectionId);
}