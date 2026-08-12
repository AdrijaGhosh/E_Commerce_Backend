package com.example.ECommerceBackend.dtos;

import lombok.*;

import java.util.List;

@Getter @Setter @Builder @AllArgsConstructor @NoArgsConstructor
public class CartResponseDTO {
    private Long cartId;
    private List<CartItemResponseDTO> items;
    private double totalAmount;
}