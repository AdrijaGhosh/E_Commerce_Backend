package com.example.ECommerceBackend.services;

import com.example.ECommerceBackend.dtos.*;
import com.example.ECommerceBackend.entities.Users;
import com.example.ECommerceBackend.repositories.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.core.AuthenticationException;
import java.util.Collections;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtService jwtService;
    @Autowired @Lazy
    private AuthenticationManager authenticationManager;

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
                .password(passwordEncoder.encode(req.getPassword()))
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
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.getEmail(),req.getPassword())
            );
        }
        catch (AuthenticationException e)
        {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Password did not match");
        }

            String token=jwtService.generateToken(user.getEmail(),user.getId(),user.getRole());
                return UserResponseDTO.builder().name(user.getName()).email(user.getEmail()).role(user.getRole()).token(token).build();
    }

    public UserProfileDTO getUserProfile(Long id) {
        Users user=usersRepository.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"User not found"));
        return UserProfileDTO.builder().name(user.getName())
                .email(user.getEmail())
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
                .email(updatedUser.getEmail())
                .phone(updatedUser.getPhone())
                .address(updatedUser.getAddress())
                .role(updatedUser.getRole())
                .build();
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Users user1=usersRepository.findByEmail(email);
        if(user1==null)
            throw new UsernameNotFoundException("User not found with email :"+email);
        return User.builder().username(user1.getEmail()).password(user1.getPassword())
                .authorities(Collections.emptyList()).build();
    }
}
