package com.foodbev.FoodBevApp.dto.user;

import lombok.Data;

@Data
public class UserLoginDto {
    private String email;
    private String password;
}