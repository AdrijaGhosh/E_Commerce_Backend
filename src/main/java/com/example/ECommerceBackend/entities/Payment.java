package com.example.ECommerceBackend.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Payment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private double amount;
    private String status;
    private String method;
    private String razorpayOrderId;
    private String razorpayPaymentId;
    @Column(insertable = false, updatable = false)
    private LocalDateTime createdAt;
    @OneToOne(mappedBy = "payment")
    private Order order;

}