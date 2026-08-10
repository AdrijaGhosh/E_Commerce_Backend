package com.example.ECommerceBackend.dtos;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDTO {

    private String name;
    private String email;
    private String role;
    private String token;
}