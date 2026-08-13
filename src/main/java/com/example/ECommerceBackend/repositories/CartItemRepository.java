package com.example.ECommerceBackend.repositories;

import com.example.ECommerceBackend.entities.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem,Long> {
    Optional<CartItem> findByCartIdAndProductId(Long cartId,Long productId);
}
