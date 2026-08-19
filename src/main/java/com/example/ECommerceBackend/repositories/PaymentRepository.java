package com.example.ECommerceBackend.repositories;

import com.example.ECommerceBackend.entities.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}