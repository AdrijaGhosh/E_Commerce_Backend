package com.example.ECommerceBackend.dtos;

import lombok.*;

@Getter @Setter @Builder @AllArgsConstructor @NoArgsConstructor
public class InitiateOrderResponseDTO {
    private Long internalOrderId;
    private String razorpayOrderId;
    private double amount;
    private String currency;
    private String razorpayKeyId;
}