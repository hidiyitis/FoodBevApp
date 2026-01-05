package com.foodbev.FoodBevApp.entity.product;

import com.foodbev.FoodBevApp.entity.product.enums.FoodType;
import com.foodbev.FoodBevApp.entity.product.enums.ProductCategory;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "food_products")
@DiscriminatorValue("FOOD")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Food extends Product {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FoodType foodType;

    private String cuisineType;

    @Column(nullable = false)
    private Integer preparationTime = 15;

    private Boolean isVegetarian = false;

    @Override
    protected void onCreate() {
        super.onCreate();
        setCategory(ProductCategory.FOOD);
    }


    @Override
    public BigDecimal calculatePrice() {
        BigDecimal price = getBasePrice();

        if (Boolean.TRUE.equals(isVegetarian)) {
            price = price.subtract(new BigDecimal("5000"));
        }

        return price;
    }
}
