package com.example.ECommerceBackend.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Payment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private double amount;
    private String status;
    private String method;
    @Column(insertable = false, updatable = false)
    private LocalDateTime createdAt;
    @OneToOne(mappedBy = "payment")
    private Order order;

}