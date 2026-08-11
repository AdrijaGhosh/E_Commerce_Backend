package com.example.ECommerceBackend.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
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
    @OneToMany(mappedBy = "order",cascade = CascadeType.ALL)
    private List<OrderItem> orderItems;
    @OneToOne(cascade = CascadeType.ALL)
    private Payment payment;
}
