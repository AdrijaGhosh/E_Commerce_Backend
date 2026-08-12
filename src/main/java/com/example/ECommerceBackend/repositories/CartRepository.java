package com.example.ECommerceBackend.repositories;

import com.example.ECommerceBackend.entities.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {
}