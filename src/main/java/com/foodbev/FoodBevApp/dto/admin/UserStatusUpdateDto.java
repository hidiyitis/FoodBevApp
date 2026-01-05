package com.foodbev.FoodBevApp.dto.admin;

import lombok.Data;

@Data
public class UserStatusUpdateDto {
  private Long userId;
  private Boolean isActive;
}
