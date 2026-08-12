package com.example.ECommerceBackend.controllers;

import com.example.ECommerceBackend.dtos.ProductRequestDTO;
import com.example.ECommerceBackend.dtos.ProductResponseDTO;
import com.example.ECommerceBackend.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponseDTO> addProduct(@RequestBody ProductRequestDTO req)
    {
        return ResponseEntity.ok(productService.addProduct(req));
    }

    @GetMapping
    public ResponseEntity<List<ProductResponseDTO>> showAllProducts()
    {
        return ResponseEntity.ok(productService.showAllProducts());
    }
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> showProduct(@PathVariable Long id)
    {
        return ResponseEntity.ok(productService.showProduct(id));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ProductResponseDTO>> showProductByCategory(@PathVariable Long categoryId)
    {
        return ResponseEntity.ok(productService.showProductByCategory(categoryId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> updateProduct(@RequestBody ProductRequestDTO requestDTO,@PathVariable Long id)
    {
        return ResponseEntity.ok(productService.updateProduct(requestDTO,id));
    }
}
