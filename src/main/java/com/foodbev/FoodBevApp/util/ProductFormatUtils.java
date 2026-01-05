package com.foodbev.FoodBevApp.util;

import com.foodbev.FoodBevApp.entity.product.Coffee;
import com.foodbev.FoodBevApp.entity.product.Food;
import com.foodbev.FoodBevApp.entity.product.Product;
import com.foodbev.FoodBevApp.entity.product.Snack;
import com.foodbev.FoodBevApp.entity.product.enums.ProductStatus;

import java.text.NumberFormat;
import java.util.Locale;

public class ProductFormatUtils {
    public ProductFormatUtils(){

    }

    public static String formatCategoryDisplay(Product product) {
        String baseCategory = product.getCategory().toString();
        if (product instanceof Coffee coffee) {
            return String.format("%s (%s - %s)", baseCategory, coffee.getCoffeeType(), coffee.getSize());
        } else if (product instanceof Food food) {
            return String.format("%s (%s)", baseCategory, food.getFoodType());
        } else if (product instanceof Snack snack) {
            return String.format("%s (%s)", baseCategory, snack.getSnackType());
        }
        return baseCategory;
    }

    public static String formatStatusBadge(ProductStatus status) {
        return switch (status) {
            case IN_STOCK -> "In Stock";
            case OUT_OF_STOCK -> "Out of Stock";
            case DISCONTINUED -> "Discontinued";
        };
    }

    public static String formatPrice(java.math.BigDecimal price) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        return formatter.format(price);
    }
}
