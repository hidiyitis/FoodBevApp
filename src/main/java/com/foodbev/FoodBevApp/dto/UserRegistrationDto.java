package com.foodbev.FoodBevApp.dto;

import lombok.Data;

@Data
public class UserRegistrationDto {
    private String name;
    private String phone;
    private String email;
    private String password;
    private String confirmPassword;
}