package com.foodbev.FoodBevApp.dto.product.request;

import com.foodbev.FoodBevApp.entity.product.enums.SnackType;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SnackRequest extends ProductBaseRequest {

    @NotNull(message = "Snack type is required")
    private SnackType snackType;

    @Min(value = 0, message = "Calories cannot be negative")
    @Max(value = 2000, message = "Calories cannot exceed 2000")
    private Integer calories;

    @NotNull(message = "Gluten-free status is required")
    private Boolean isGlutenFree;
}
