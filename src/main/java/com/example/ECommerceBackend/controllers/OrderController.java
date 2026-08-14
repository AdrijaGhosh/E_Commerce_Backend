package com.example.ECommerceBackend.controllers;

import com.example.ECommerceBackend.dtos.OrderResponseDTO;
import com.example.ECommerceBackend.dtos.OrderStatusChangeDTO;
import com.example.ECommerceBackend.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/place")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<OrderResponseDTO> placeOrder()
    {
        return ResponseEntity.ok(orderService.placeOrder());
    }
    @GetMapping("/user")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<OrderResponseDTO>> getAllOrdersofUser()
    {
        return ResponseEntity.ok(orderService.getAllOrderOfUser());
    }
    @GetMapping("/{orderId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<OrderResponseDTO> getOrderByOrderId(@PathVariable Long orderId)
    {
        return ResponseEntity.ok(orderService.getOrderByOrderId(orderId));
    }
    @PutMapping("/{orderId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderResponseDTO> changeStatusofOrder(@PathVariable Long orderId, @RequestBody OrderStatusChangeDTO statusChangeDTO)
    {
        return ResponseEntity.ok(orderService.changeStatusofOrder(orderId,statusChangeDTO));
    }
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<OrderResponseDTO>> getAllOrders()
    {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

}
