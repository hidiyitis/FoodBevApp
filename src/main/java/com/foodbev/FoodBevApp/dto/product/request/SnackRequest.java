package com.foodbev.FoodBevApp.dto.product.request;

import com.foodbev.FoodBevApp.entity.product.enums.SnackType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SnackRequest extends ProductBaseRequest {

    @NotNull(message = "Snack type is required")
    private SnackType snackType;

    @Min(value = 0, message = "Calories cannot be negative")
    private Integer calories;

    private Boolean isGlutenFree = false;
}
