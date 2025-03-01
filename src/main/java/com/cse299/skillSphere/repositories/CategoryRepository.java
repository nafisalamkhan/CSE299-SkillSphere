package com.cse299.skillSphere.repositories;

import com.cse299.skillSphere.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
}