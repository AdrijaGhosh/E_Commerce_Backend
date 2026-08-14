package com.example.ECommerceBackend.repositories;

import com.example.ECommerceBackend.entities.Order;
import com.example.ECommerceBackend.entities.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order,Long> {
    List<Order> findByUsers(Users users);
}
