package com.foodbev.FoodBevApp.dto.product.response;

import com.foodbev.FoodBevApp.entity.product.enums.ProductCategory;
import com.foodbev.FoodBevApp.entity.product.enums.ProductStatus;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductListResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal basePrice;
    private BigDecimal calculatedPrice;
    private ProductCategory category;
    private ProductStatus status;
    private String imageUrl;
    private String categoryDisplay;
    private String statusBadge;
    private String priceFormatted;
}
