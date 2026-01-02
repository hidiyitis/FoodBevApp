package com.foodbev.FoodBevApp.dto.product.response;

import com.foodbev.FoodBevApp.entity.product.enums.SnackType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SnackResponse extends ProductBaseResponse {

    private SnackType snackType;
    private Integer calories;
    private Boolean isGlutenFree;
    private String glutenFreeBadge;
    private String caloriesDisplay;
}
