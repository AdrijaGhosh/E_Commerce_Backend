package com.example.ECommerceBackend.controllers;


import com.example.ECommerceBackend.dtos.*;
import com.example.ECommerceBackend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UsersController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> registerUser(@RequestBody UserRegisterRequestDTO req )
    {
        return ResponseEntity.ok(userService.registerUser(req));
    }
    @PostMapping("/login")
    public ResponseEntity<UserResponseDTO> loginUser(@RequestBody UserLoginRequestDTO req)
    {
        return ResponseEntity.ok(userService.loginUser(req));
    }
    @GetMapping("/{id}")
    public ResponseEntity<UserProfileDTO> getUserProfile(@PathVariable Long id)
    {
        return ResponseEntity.ok(userService.getUserProfile(id));
    }
    @PutMapping("/{id}")
    public ResponseEntity<UserProfileDTO> updateUserProfile(@PathVariable Long id, @RequestBody UserUpdateDTO req)
    {
        return ResponseEntity.ok(userService.updateProfile(id,req));
    }

}
