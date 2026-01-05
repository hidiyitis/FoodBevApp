package com.foodbev.FoodBevApp.service.user;

import com.foodbev.FoodBevApp.dto.admin.AdminUserRegistrationDto;
import com.foodbev.FoodBevApp.dto.user.UserRegistrationDto;
import com.foodbev.FoodBevApp.entity.user.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Optional;

public interface UserService extends UserDetailsService {
    User save(UserRegistrationDto registrationDto);

    User findByEmail(String email);

    boolean existsByPhone(String phone);

    // Admin user management methods
    User saveAdmin(AdminUserRegistrationDto dto);

    List<User> findAllUsers();

    List<User> findAllAdmins();

    Optional<User> findById(Long id);

    User updateUserStatus(Long userId, boolean isActive);
}