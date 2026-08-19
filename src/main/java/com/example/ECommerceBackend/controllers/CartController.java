package com.example.ECommerceBackend.controllers;

import com.example.ECommerceBackend.dtos.AddToCartRequestDTO;
import com.example.ECommerceBackend.dtos.CartResponseDTO;
import com.example.ECommerceBackend.dtos.UpdateCartItemRequestDTO;
import com.example.ECommerceBackend.services.CartService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CartResponseDTO> getCart(@PathVariable Long userId) {
        return ResponseEntity.ok(cartService.getCartByUserId(userId));
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CartResponseDTO> getMyCart() {
        return ResponseEntity.ok(cartService.getMyCart());
    }

    @PostMapping("/add")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CartResponseDTO> addtoCart(@Valid @RequestBody AddToCartRequestDTO req)
    {
        return ResponseEntity.ok(cartService.addToCart(req));
    }

    @PutMapping("/{cartItemId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CartResponseDTO> updateCart(@Valid @RequestBody UpdateCartItemRequestDTO req, @PathVariable Long cartItemId)
    {
        return ResponseEntity.ok(cartService.updateCart(req,cartItemId));
    }
    @PutMapping("/addOne/{cartItemId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity <CartResponseDTO> addOneCart(@PathVariable Long cartItemId)
    {
        return ResponseEntity.ok(cartService.addOneItem(cartItemId));
    }
    @PutMapping("/removeOne/{cartItemId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CartResponseDTO> removeOneCart(@PathVariable Long cartItemId)
    {
        return ResponseEntity.ok(cartService.removeByOne(cartItemId));
    }

    @DeleteMapping("/{cartItemId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CartResponseDTO> deleteItemFromCart(@PathVariable Long cartItemId)
    {
        return ResponseEntity.ok(cartService.deleteItemFromCart(cartItemId));
    }
}