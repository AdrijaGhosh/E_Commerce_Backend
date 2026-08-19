package com.example.ECommerceBackend.controllers;


import com.example.ECommerceBackend.dtos.*;
import com.example.ECommerceBackend.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UsersController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> registerUser(@Valid @RequestBody UserRegisterRequestDTO req )
    {
        return ResponseEntity.ok(userService.registerUser(req));
    }
    @PostMapping("/login")
    public ResponseEntity<UserResponseDTO> loginUser(@RequestBody UserLoginRequestDTO req)
    {
        return ResponseEntity.ok(userService.loginUser(req));
    }
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isSelf(#id, authentication)")
    public ResponseEntity<UserProfileDTO> getUserProfile(@PathVariable Long id)
    {
        return ResponseEntity.ok(userService.getUserProfile(id));
    }
    @PutMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserProfileDTO> updateUserProfile(@Valid @RequestBody UserUpdateDTO req)
    {
        return ResponseEntity.ok(userService.updateProfile(req));
    }

}
