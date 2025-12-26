package com.foodbev.FoodBevApp.dto.product.response;

import com.foodbev.FoodBevApp.entity.product.enums.FoodType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FoodResponse extends ProductBaseResponse {

    private FoodType foodType;
    private String cuisineType;
    private Integer preparationTime;
    private Boolean isVegetarian;
    private String preparationTimeDisplay;
    private String vegetarianBadge;
}
