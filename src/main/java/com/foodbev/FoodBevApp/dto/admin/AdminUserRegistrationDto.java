package com.foodbev.FoodBevApp.dto.admin;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class AdminUserRegistrationDto {

  @NotBlank(message = "Name is required")
  private String name;

  @NotBlank(message = "Email is required")
  @Email(message = "Please provide a valid email")
  private String email;

  @NotBlank(message = "Phone is required")
  @Pattern(regexp = "^[0-9]{10,15}$", message = "Phone must be 10-15 digits")
  private String phone;

  @NotBlank(message = "Password is required")
  @Size(min = 8, message = "Password must be at least 8 characters")
  private String password;

  @NotBlank(message = "Confirm password is required")
  private String confirmPassword;
}
