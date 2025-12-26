package com.foodbev.FoodBevApp.dto.product.request;

import com.foodbev.FoodBevApp.entity.product.enums.CoffeeType;
import com.foodbev.FoodBevApp.entity.product.enums.ProductSize;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CoffeeRequest extends ProductBaseRequest {

    @NotNull(message = "Coffee type is required")
    private CoffeeType coffeeType;

    @NotNull(message = "Size is required")
    private ProductSize size;

    @NotNull(message = "Temperature preference is required")
    private Boolean isHot;

    @NotNull(message = "Espresso shots is required")
    @Min(value = 1, message = "Minimum 1 espresso shot")
    @Max(value = 5, message = "Maximum 5 espresso shots")
    private Integer espressoShots;
}
