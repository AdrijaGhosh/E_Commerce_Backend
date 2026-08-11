package com.example.ECommerceBackend.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private double price;
    private int stock;
    @ManyToOne
    @JoinColumn(name = "category_id" )
    private Category category;
    @OneToMany(mappedBy = "product")
    private List<CartItem> cartItems;
    @OneToMany(mappedBy = "product")
    private List<OrderItem> orderItems;

}