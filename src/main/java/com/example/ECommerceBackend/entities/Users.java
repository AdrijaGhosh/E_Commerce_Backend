package com.example.ECommerceBackend.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
 @Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Users {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Column(unique = true)
    private String email;
    private String password;
    private String phone;
    private String address;
    private String role;
    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;
    @OneToOne(cascade = CascadeType.ALL)
    private Cart cart;
    @OneToMany(mappedBy = "users")
    private List<Order> orders;
}
