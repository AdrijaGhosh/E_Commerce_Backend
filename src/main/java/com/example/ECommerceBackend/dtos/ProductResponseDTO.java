package com.example.ECommerceBackend.dtos;

import lombok.*;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponseDTO implements Serializable {
    private Long id;
    private String name;
    private String description;
    private double price;
    private int stock;
    private String categoryName;
    private List<String> imageUrls;
}

