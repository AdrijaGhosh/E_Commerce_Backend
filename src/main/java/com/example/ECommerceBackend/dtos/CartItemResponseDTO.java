package com.example.ECommerceBackend.dtos;

import lombok.*;

@Getter @Setter @Builder @AllArgsConstructor @NoArgsConstructor
public class CartItemResponseDTO {
    private Long cartItemId;
    private Long productId;
    private String productName;
    private double price;
    private int quantity;
}