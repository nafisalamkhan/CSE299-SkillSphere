package com.cse299.skillSphere.repositories;

import com.cse299.skillSphere.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

    Optional<Category> findByNameEqualsIgnoreCase(String name);
}