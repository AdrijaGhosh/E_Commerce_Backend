package com.example.ECommerceBackend.repositories;

import com.example.ECommerceBackend.dtos.CategoryRequestDTO;
import com.example.ECommerceBackend.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category,Long> {
    boolean existsByName(String name);

}
