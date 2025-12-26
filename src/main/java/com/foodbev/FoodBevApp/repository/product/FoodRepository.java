package com.foodbev.FoodBevApp.repository.product;

import com.foodbev.FoodBevApp.entity.product.Food;
import com.foodbev.FoodBevApp.entity.product.enums.FoodType;
import com.foodbev.FoodBevApp.entity.product.enums.ProductStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FoodRepository extends JpaRepository<Food, Long> {

    // Find by food type
    List<Food> findByFoodType(FoodType foodType);

    // Find by cuisine type
    List<Food> findByCuisineTypeContainingIgnoreCase(String cuisineType);

    // Find vegetarian foods
    List<Food> findByIsVegetarian(Boolean isVegetarian);

    // Find by food type and status
    List<Food> findByFoodTypeAndStatus(FoodType foodType, ProductStatus status);

    // Find available vegetarian foods
    @Query("SELECT f FROM Food f WHERE f.isVegetarian = true AND f.status = 'IN_STOCK'")
    List<Food> findAvailableVegetarianFoods();

    // Find by preparation time less than or equal
    List<Food> findByPreparationTimeLessThanEqual(Integer maxTime);

    // Find quick available foods (preparation time <= 15 minutes)
    @Query("SELECT f FROM Food f WHERE f.preparationTime <= 15 AND f.status = 'IN_STOCK'")
    List<Food> findQuickAvailableFoods();

    // Find by cuisine type and vegetarian option
    @Query("SELECT f FROM Food f WHERE f.cuisineType = :cuisineType AND f.isVegetarian = :isVegetarian")
    List<Food> findByCuisineTypeAndIsVegetarian(@Param("cuisineType") String cuisineType,
                                                @Param("isVegetarian") Boolean isVegetarian);
}
