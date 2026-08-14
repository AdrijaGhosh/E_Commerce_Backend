package com.example.ECommerceBackend.controllers;

import com.example.ECommerceBackend.dtos.ProductRequestDTO;
import com.example.ECommerceBackend.dtos.ProductResponseDTO;
import com.example.ECommerceBackend.dtos.UpdateStockRequestDTO;
import com.example.ECommerceBackend.repositories.ProductRepository;
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

    @Autowired
    private ProductRepository productRepository;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponseDTO> addProduct(@RequestBody ProductRequestDTO req)
    {
        return ResponseEntity.ok(productService.addProduct(req));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ProductResponseDTO>> showAllProducts()
    {
        return ResponseEntity.ok(productService.showAllProducts());
    }
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ProductResponseDTO> showProduct(@PathVariable Long id)
    {
        return ResponseEntity.ok(productService.showProduct(id));
    }

    @GetMapping("/category/{categoryId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ProductResponseDTO>> showProductByCategory(@PathVariable Long categoryId)
    {
        return ResponseEntity.ok(productService.showProductByCategory(categoryId));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponseDTO> updateProduct(@RequestBody ProductRequestDTO requestDTO,@PathVariable Long id)
    {
        return ResponseEntity.ok(productService.updateProduct(requestDTO,id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id)
    {
        productRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/stock")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponseDTO> increaseStock(@PathVariable Long id, @RequestBody UpdateStockRequestDTO req) {
        return ResponseEntity.ok(productService.increaseStock(id, req));
    }
}
