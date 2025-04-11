package com.cse299.skillSphere.repositories;

import com.cse299.skillSphere.models.UserVideoProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserVideoProgressRepository extends JpaRepository<UserVideoProgress, Long> {

    List<UserVideoProgress> findByUserIdAndCourseId(Integer userId, Integer courseId);

    Optional<UserVideoProgress> findByUserIdAndCourseIdAndVideoId(Integer userId, Integer courseId, Integer videoId);

    long countByUserIdAndCourseIdAndWatched(Integer userId, Integer courseId, boolean watched);

    @Query("SELECT p FROM UserVideoProgress p WHERE p.userId = :userId AND p.courseId = :courseId " +
           "ORDER BY p.lastWatchedDate DESC LIMIT 1")
    Optional<UserVideoProgress> findMostRecentProgress(@Param("userId") Integer userId, @Param("courseId") Integer courseId);

    @Transactional
    @Modifying
    @Query("DELETE FROM UserVideoProgress p WHERE p.userId = :userId AND p.courseId = :courseId")
    void deleteAllByUserIdAndCourseId(@Param("userId") Integer userId, @Param("courseId") Integer courseId);
}