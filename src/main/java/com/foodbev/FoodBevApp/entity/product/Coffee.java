package com.foodbev.FoodBevApp.entity.product;

import com.foodbev.FoodBevApp.entity.product.enums.CoffeeType;
import com.foodbev.FoodBevApp.entity.product.enums.ProductCategory;
import com.foodbev.FoodBevApp.entity.product.enums.ProductSize;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "coffee_products")
@DiscriminatorValue("COFFEE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Coffee extends Product {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CoffeeType coffeeType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductSize size = ProductSize.MEDIUM;

    @Column(nullable = false)
    private Boolean isHot = true;

    @Column(nullable = false)
    private Integer espressoShots = 1;

    @Override
    protected void onCreate() {
        super.onCreate();
        setCategory(ProductCategory.COFFEE);
    }

    @Override
    public BigDecimal calculatePrice() {
        BigDecimal price = getBasePrice();

        switch (size) {
            case SMALL -> price = price.subtract(new BigDecimal("5000"));
            case LARGE -> price = price.add(new BigDecimal("8000"));
        }

        if (espressoShots > 1) {
            BigDecimal extraShotPrice = new BigDecimal("5000")
                    .multiply(BigDecimal.valueOf(espressoShots - 1));
            price = price.add(extraShotPrice);
        }

        return price;
    }
}

