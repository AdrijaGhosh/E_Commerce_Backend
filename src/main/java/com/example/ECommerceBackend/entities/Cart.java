package com.example.ECommerceBackend.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity @Getter @Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Cart {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(mappedBy = "cart")
    private Users users;
    @OneToMany(mappedBy = "cart",cascade = CascadeType.ALL)
    private List<CartItem> cartItems;
}
