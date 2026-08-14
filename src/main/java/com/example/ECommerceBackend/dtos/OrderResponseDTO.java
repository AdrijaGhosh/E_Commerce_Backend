package com.example.ECommerceBackend.dtos;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponseDTO {
    private Long orderId;
    private String status;
    private double totalAmount;
    private List<OrderItemResponseDTO> items;
}
