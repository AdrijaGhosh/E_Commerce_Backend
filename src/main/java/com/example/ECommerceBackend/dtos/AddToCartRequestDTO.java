package com.example.ECommerceBackend.dtos;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddToCartRequestDTO {
    private Long productId;
    private int quantity;
}
