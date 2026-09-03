package com.example.ECommerceBackend.dtos;

import lombok.*;

import java.io.Serializable;
import java.util.List;

@Getter @Setter @Builder @AllArgsConstructor @NoArgsConstructor
public class PagedProductResponseDTO implements Serializable {
    private List<ProductResponseDTO> products;
    private int currentPage;
    private int totalPages;
    private long totalItems;
}