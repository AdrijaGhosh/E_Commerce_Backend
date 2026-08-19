package com.example.ECommerceBackend.dtos;

import jakarta.validation.constraints.Min;
import lombok.*;

@Getter @Setter @Builder @AllArgsConstructor @NoArgsConstructor
public class UpdateCartItemRequestDTO {
    @Min(value = 1, message = "Quantity cannot be less than 1")
    private int quantity;
}