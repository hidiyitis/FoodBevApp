package com.foodbev.FoodBevApp.dto.product.request;

import com.foodbev.FoodBevApp.entity.product.enums.CoffeeType;
import com.foodbev.FoodBevApp.entity.product.enums.ProductSize;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CoffeeRequest extends ProductBaseRequest {

    @NotNull(message = "Coffee type is required")
    private CoffeeType coffeeType;

    private ProductSize size = ProductSize.MEDIUM;

    private Boolean isHot = true;

    @Min(value = 1, message = "Espresso shots must be at least 1")
    @Max(value = 5, message = "Espresso shots cannot exceed 5")
    private Integer espressoShots = 1;
}
