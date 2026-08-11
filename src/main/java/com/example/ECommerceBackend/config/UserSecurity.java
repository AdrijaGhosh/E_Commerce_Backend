package com.example.ECommerceBackend.config;

import com.example.ECommerceBackend.repositories.UsersRepository;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("userSecurity")
public class UserSecurity {

    @Autowired
    private UsersRepository usersRepository;

    public Boolean isSelf(Long id, Authentication authentication)
    {
        String loggedInEmail=authentication.getName();
        return usersRepository.findById(id).map(
                user->user.getEmail().equals(loggedInEmail)
        ).orElse(false);
    }
}
