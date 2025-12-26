package com.foodbev.FoodBevApp.entity.product;

import com.foodbev.FoodBevApp.entity.product.enums.ProductCategory;
import com.foodbev.FoodBevApp.entity.product.enums.SnackType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "snack_products")
@DiscriminatorValue("SNACK")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Snack extends Product {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SnackType snackType;

    private Integer calories;

    @Column(nullable = false)
    private Boolean isGlutenFree = false;

    @Override
    protected void onCreate() {
        super.onCreate();
        setCategory(ProductCategory.SNACK);
    }

    @Override
    public BigDecimal calculatePrice() {
        BigDecimal price = getBasePrice();

        if (isGlutenFree) {
            price = price.add(new BigDecimal("3000"));
        }

        return price;
    }
}

