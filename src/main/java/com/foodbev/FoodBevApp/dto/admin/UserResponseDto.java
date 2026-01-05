package com.foodbev.FoodBevApp.dto.admin;

import com.foodbev.FoodBevApp.entity.user.User;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserResponseDto {
  private Long id;
  private String name;
  private String email;
  private String phone;
  private String role;
  private Boolean isActive;
  private LocalDateTime registrationDate;

  public static UserResponseDto fromEntity(User user) {
    UserResponseDto dto = new UserResponseDto();
    dto.setId(user.getId());
    dto.setName(user.getName());
    dto.setEmail(user.getEmail());
    dto.setPhone(user.getPhone());
    dto.setRole(user.getRole());
    dto.setIsActive(user.getIsActive());
    dto.setRegistrationDate(user.getRegistrationDate());
    return dto;
  }
}
