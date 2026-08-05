package com.example.ECommerceBackend.repositories;

import com.example.ECommerceBackend.entities.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {
    boolean existsByEmail(String email);

    Users findByEmail(String email);
}