package com.example.ECommerceBackend.services;

import com.example.ECommerceBackend.dtos.PagedProductResponseDTO;
import com.example.ECommerceBackend.dtos.ProductRequestDTO;
import com.example.ECommerceBackend.dtos.ProductResponseDTO;
import com.example.ECommerceBackend.dtos.UpdateStockRequestDTO;
import com.example.ECommerceBackend.entities.Category;
import com.example.ECommerceBackend.entities.Product;
import com.example.ECommerceBackend.entities.ProductImage;
import com.example.ECommerceBackend.repositories.CategoryRepository;
import com.example.ECommerceBackend.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
@Service
public class ProductService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @CacheEvict(value = "products", allEntries = true)
    public ProductResponseDTO addProduct(ProductRequestDTO req) {
        Category category = categoryRepository.findById(req.getCategoryId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));
        Product product = Product.builder().name(req.getName()).description(req.getDescription())
                .price(req.getPrice()).stock(req.getStock()).category(category).build();

        if (req.getImageUrls() != null) {
            List<ProductImage> images = req.getImageUrls().stream().map(
                    url -> ProductImage.builder().url(url).product(product).build()
            ).toList();
            product.setImages(images);
        }
        Product saved = productRepository.save(product);
        return ProductResponseDTO.builder().name(saved.getName()).description(saved.getDescription()).id(saved.getId())
                .price(saved.getPrice()).stock(saved.getStock()).categoryName(saved.getCategory().getName())
                .imageUrls(saved.getImages() != null ?
                        saved.getImages().stream().map(ProductImage::getUrl).toList()
                        : List.of()).build();
    }


    @Cacheable(value = "products", key = "'page_' + #page + '_' + #size")
    public PagedProductResponseDTO showAllProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productPage = productRepository.findAll(pageable);

        List<ProductResponseDTO> productDTOs = productPage.getContent().stream()
                .map(product -> ProductResponseDTO.builder()
                        .name(product.getName())
                        .id(product.getId())
                        .description(product.getDescription())
                        .price(product.getPrice())
                        .stock(product.getStock())
                        .imageUrls(
                                product.getImages() != null
                                        ? product.getImages().stream().map(ProductImage::getUrl).toList()
                                        : List.of()
                        )
                        .categoryName(product.getCategory().getName())
                        .build())
                .toList();

        return PagedProductResponseDTO.builder()
                .products(productDTOs)
                .currentPage(page)
                .totalPages(productPage.getTotalPages())
                .totalItems(productPage.getTotalElements())
                .build();
    }

    @Cacheable(value = "products", key = "#id")
    public ProductResponseDTO showProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found with given id"));
        return ProductResponseDTO.builder().name(product.getName()).id(product.getId())
                .description(product.getDescription()).price(product.getPrice()).stock(product.getStock())
                .categoryName(product.getCategory().getName())
                .imageUrls(product.getImages() != null ? product.getImages().stream().map(ProductImage::getUrl).toList() : List.of())
                .build();
    }

    @Cacheable(value = "products", key = "'category_' + #categoryId")
    public List<ProductResponseDTO> showProductByCategory(Long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found");
        }
        List<Product> products = productRepository.findByCategoryIdWithImages(categoryId);
        return products.stream().map(
                product -> ProductResponseDTO.builder()
                        .id(product.getId())
                        .name(product.getName())
                        .description(product.getDescription())
                        .price(product.getPrice())
                        .stock(product.getStock())
                        .categoryName(product.getCategory().getName())
                        .imageUrls(
                                product.getImages() != null
                                        ? product.getImages().stream().map(ProductImage::getUrl).toList()
                                        : List.of()
                        )
                        .build()
        ).toList();
    }

    @CacheEvict(value = "products", allEntries = true)
    public ProductResponseDTO updateProduct(ProductRequestDTO requestDTO, Long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
        product.setName(requestDTO.getName());
        product.setDescription(requestDTO.getDescription());
        product.setPrice(requestDTO.getPrice());
        product.setStock(requestDTO.getStock());
        product.setCategory(categoryRepository.findById(requestDTO.getCategoryId()).orElse(product.getCategory()));
        product.getImages().clear();
        if (requestDTO.getImageUrls() != null) {
            List<ProductImage> newImages = requestDTO.getImageUrls().stream()
                    .map(url -> ProductImage.builder().url(url).product(product).build())
                    .toList();
            product.getImages().addAll(newImages);
        }
        Product updated = productRepository.save(product);

        return ProductResponseDTO.builder()
                .id(updated.getId())
                .name(updated.getName())
                .description(updated.getDescription())
                .price(updated.getPrice())
                .stock(updated.getStock())
                .categoryName(updated.getCategory().getName())
                .imageUrls(
                        updated.getImages() != null
                                ? updated.getImages().stream().map(ProductImage::getUrl).toList()
                                : List.of()
                )
                .build();
    }

    @CacheEvict(value = "products", allEntries = true)
    public ProductResponseDTO increaseStock(Long id, UpdateStockRequestDTO req) {
        if (req.getQuantity() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quantity to add must be positive");
        }
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
        product.setStock(product.getStock() + req.getQuantity());
        Product updated = productRepository.save(product);
        return ProductResponseDTO.builder()
                .id(updated.getId())
                .name(updated.getName())
                .description(updated.getDescription())
                .price(updated.getPrice())
                .stock(updated.getStock())
                .categoryName(updated.getCategory().getName())
                .imageUrls(
                        updated.getImages() != null
                                ? updated.getImages().stream().map(ProductImage::getUrl).toList()
                                : List.of()
                )
                .build();
    }

    @CacheEvict(value = "products", allEntries = true)
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
        }
        productRepository.deleteById(id);
    }

    // Hook for OrderService to call after stock changes during checkout
    @CacheEvict(value = "products", allEntries = true)
    public void evictProductCache() {
        // intentionally empty
    }
}