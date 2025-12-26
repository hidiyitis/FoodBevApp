package com.foodbev.FoodBevApp.dto.product.request;

import com.foodbev.FoodBevApp.entity.product.enums.ProductStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductStatusRequest {

    @NotNull(message = "Status is required")
    private ProductStatus status;
}
