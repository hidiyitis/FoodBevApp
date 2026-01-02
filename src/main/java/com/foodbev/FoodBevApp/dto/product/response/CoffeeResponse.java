package com.foodbev.FoodBevApp.dto.product.response;

import com.foodbev.FoodBevApp.entity.product.enums.CoffeeType;
import com.foodbev.FoodBevApp.entity.product.enums.ProductSize;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CoffeeResponse extends ProductBaseResponse {

    private CoffeeType coffeeType;
    private ProductSize size;
    private Boolean isHot;
    private Integer espressoShots;
    private String temperatureDisplay;
    private String sizeDisplay;
}

