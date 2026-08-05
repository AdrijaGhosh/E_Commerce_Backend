package com.example.ECommerceBackend.dtos;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRegisterRequestDTO {
    private String name;
    private String email;
    private String password;
    private String phone;
    private String address;
    private String adminCode;
}
