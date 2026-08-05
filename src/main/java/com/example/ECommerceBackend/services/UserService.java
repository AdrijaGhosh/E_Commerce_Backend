package com.example.ECommerceBackend.services;

import com.example.ECommerceBackend.dtos.*;
import com.example.ECommerceBackend.entities.Users;
import com.example.ECommerceBackend.repositories.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserService {
    @Autowired
    private UsersRepository usersRepository;
    private static final String ADMIN_SECRET_CODE="ADMIN123";

    public UserResponseDTO registerUser(UserRegisterRequestDTO req) {
        if(usersRepository.existsByEmail(req.getEmail()))
        {
            throw new RuntimeException("Email already registered");
        }
        String role=ADMIN_SECRET_CODE.equals(req.getAdminCode())?"ADMIN":"CUSTOMER";
        Users user=Users.builder()
                .name(req.getName())
                .email(req.getEmail())
                .password(req.getPassword())
                .phone(req.getPhone())
                .address(req.getAddress())
                .role(role)
                .build();
        Users savedUser=usersRepository.save(user);
        return UserResponseDTO.builder()
                .name(savedUser.getName())
                .email(savedUser.getEmail())
                .role(savedUser.getRole())
                .build();

    }

    public UserResponseDTO loginUser(UserLoginRequestDTO req) {
        Users user=usersRepository.findByEmail(req.getEmail());
        if(user==null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Email not found");
        }
        if(!user.getPassword().equals(req.getPassword()))
        {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Password did not match");
        }

                return UserResponseDTO.builder().name(user.getName()).email(user.getEmail()).role(user.getRole()).build();
    }

    public UserProfileDTO getUserProfile(Long id) {
        Users user=usersRepository.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"User not found"));
        return UserProfileDTO.builder().name(user.getName())
                .email(user.getEmail())
                .password(user.getPassword())
                .phone(user.getPhone())
                .address(user.getAddress())
                .role(user.getRole())
                .build();
    }

    public UserProfileDTO updateProfile(Long id, UserUpdateDTO req) {
        Users user=usersRepository.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"User not found"));
        user.setName(req.getName());
        user.setAddress(req.getAddress());
        user.setPassword(req.getPassword());
        user.setPhone(req.getPhone());
        Users updatedUser=usersRepository.save(user);
        return UserProfileDTO.builder()
                .name(updatedUser.getName())
                .password(updatedUser.getPassword())
                .email(updatedUser.getEmail())
                .phone(updatedUser.getPhone())
                .address(updatedUser.getAddress())
                .role(updatedUser.getRole())
                .build();
    }
}
