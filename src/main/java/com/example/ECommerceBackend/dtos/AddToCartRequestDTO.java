package com.example.ECommerceBackend.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddToCartRequestDTO {
    @NotNull(message = "Product ID is required")
    private Long productId;
    @Min(value = 1 , message = "Quantity must be atleast 1")
    private int quantity;
}
