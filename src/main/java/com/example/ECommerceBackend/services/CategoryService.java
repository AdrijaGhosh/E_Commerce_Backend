package com.example.ECommerceBackend.services;

import com.example.ECommerceBackend.dtos.CategoryRequestDTO;
import com.example.ECommerceBackend.dtos.CategoryResponseDTO;
import com.example.ECommerceBackend.entities.Category;
import com.example.ECommerceBackend.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    public CategoryResponseDTO addToCategory(CategoryRequestDTO req) {
        if(!categoryRepository.existsByName(req.getName()))
        {
            Category category=Category.builder().name(req.getName()).build();
            Category saved= categoryRepository.save(category);
            return CategoryResponseDTO.builder().id(saved.getId())
                    .name(saved.getName()).build();
        }
        else {
            throw new RuntimeException("Category Already Exists");
        }
    }

    public List<CategoryResponseDTO> getAllCategories() {
        List<Category> cats= categoryRepository.findAll();
        List<CategoryResponseDTO> catDTOs=cats.stream().map(
                cat->CategoryResponseDTO.builder()
                        .id(cat.getId()).name(cat.getName()).build()
        ).toList();
        return catDTOs;
    }

    public CategoryResponseDTO getById(Long id) {
        Category category=categoryRepository.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Category not found exception"));
        return CategoryResponseDTO.builder().name(category.getName()).id(category.getId()).build();
    }

    public void deleteCategoryById(Long id) {
        if(categoryRepository.existsById(id))
             categoryRepository.deleteById(id);
        else
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Category do not exists.");
    }
}
