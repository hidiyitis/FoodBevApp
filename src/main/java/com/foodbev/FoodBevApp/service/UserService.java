package com.foodbev.FoodBevApp.service;

import com.foodbev.FoodBevApp.dto.UserRegistrationDto;
import com.foodbev.FoodBevApp.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    User save(UserRegistrationDto registrationDto);
    User findByEmail(String email);
    boolean existsByPhone(String phone);
}