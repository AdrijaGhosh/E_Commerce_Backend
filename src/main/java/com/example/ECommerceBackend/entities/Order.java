package com.example.ECommerceBackend.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "orders")

public class Order {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private double totalAmount;
    private String status;
    @Column(insertable = false,updatable = false)
    private LocalDateTime createdAt;
    @ManyToOne
    @JoinColumn(name="user_id")
    private Users users;
    @OneToMany(mappedBy = "order")
    private List<OrderItem> orderItems;
    @OneToOne
    private Payment payment;
}
