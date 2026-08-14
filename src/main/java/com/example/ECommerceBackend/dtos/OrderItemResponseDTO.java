package com.example.ECommerceBackend.dtos;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemResponseDTO {
    private Long productId;
    private String productName;
    private int quantity;
    private double priceAtPurchase;
}
