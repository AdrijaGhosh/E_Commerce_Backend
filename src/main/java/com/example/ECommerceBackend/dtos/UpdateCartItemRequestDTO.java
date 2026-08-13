package com.example.ECommerceBackend.dtos;

import lombok.*;

@Getter @Setter @Builder @AllArgsConstructor @NoArgsConstructor
public class UpdateCartItemRequestDTO {
    private int quantity;
}