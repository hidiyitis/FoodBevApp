package com.foodbev.FoodBevApp.dto.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductRankingDTO {
    private Integer rank;
    private String productName;
    private Long unitsSold;
    private Long orderCount;
}
