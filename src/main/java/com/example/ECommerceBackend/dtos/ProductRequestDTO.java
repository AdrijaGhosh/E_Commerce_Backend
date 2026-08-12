package com.example.ECommerceBackend.dtos;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequestDTO {
    private String name;
    private String description;
    private double price;
    private int stock;
    private Long categoryId;
    private List<String> imageUrls;
}
