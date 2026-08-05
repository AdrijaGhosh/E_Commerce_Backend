package com.example.ECommerceBackend.dtos;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileDTO {
    private String name;
    private String email;
    private String password;
    private String phone;
    private String address;
    private String role;
}
