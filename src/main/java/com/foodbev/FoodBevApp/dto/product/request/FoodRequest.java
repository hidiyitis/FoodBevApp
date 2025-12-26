package com.foodbev.FoodBevApp.dto.product.request;

import com.foodbev.FoodBevApp.entity.product.enums.FoodType;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FoodRequest extends ProductBaseRequest {

    @NotNull(message = "Food type is required")
    private FoodType foodType;

    @Size(max = 50, message = "Cuisine type cannot exceed 50 characters")
    private String cuisineType;

    @NotNull(message = "Preparation time is required")
    @Min(value = 1, message = "Preparation time must be at least 1 minute")
    @Max(value = 120, message = "Preparation time cannot exceed 120 minutes")
    private Integer preparationTime;

    @NotNull(message = "Vegetarian status is required")
    private Boolean isVegetarian;
}

