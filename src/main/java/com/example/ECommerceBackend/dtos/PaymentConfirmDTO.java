package com.example.ECommerceBackend.dtos;

import lombok.*;

@Getter @Setter @Builder @AllArgsConstructor @NoArgsConstructor
public class PaymentConfirmDTO {
    private Long internalOrderId;
    private String razorpayPaymentId;
    private String razorpaySignature;
}