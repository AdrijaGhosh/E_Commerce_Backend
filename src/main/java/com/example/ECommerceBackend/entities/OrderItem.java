package com.example.ECommerceBackend.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class OrderItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int quantity;
    private double priceAtPurchase;
    @ManyToOne
    @JoinColumn(name="order_id",nullable = false)
    private Order order;
    @ManyToOne
    @JoinColumn(name="product_id")
    private Product product;


}