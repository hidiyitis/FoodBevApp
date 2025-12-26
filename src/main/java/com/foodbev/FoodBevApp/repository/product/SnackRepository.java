package com.foodbev.FoodBevApp.repository.product;

import com.foodbev.FoodBevApp.entity.product.Snack;
import com.foodbev.FoodBevApp.entity.product.enums.SnackType;
import com.foodbev.FoodBevApp.entity.product.enums.ProductStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SnackRepository extends JpaRepository<Snack, Long> {

    // Find by snack type
    List<Snack> findBySnackType(SnackType snackType);

    // Find gluten-free snacks
    List<Snack> findByIsGlutenFree(Boolean isGlutenFree);

    // Find by snack type and status
    List<Snack> findBySnackTypeAndStatus(SnackType snackType, ProductStatus status);

    // Find available gluten-free snacks
    @Query("SELECT s FROM Snack s WHERE s.isGlutenFree = true AND s.status = 'IN_STOCK'")
    List<Snack> findAvailableGlutenFreeSnacks();

    // Find by calories less than or equal
    List<Snack> findByCaloriesLessThanEqual(Integer maxCalories);

    // Find low-calorie available snacks (< 200 calories)
    @Query("SELECT s FROM Snack s WHERE s.calories < 200 AND s.status = 'IN_STOCK'")
    List<Snack> findLowCalorieAvailableSnacks();

    // Find by snack type and gluten-free option
    @Query("SELECT s FROM Snack s WHERE s.snackType = :snackType AND s.isGlutenFree = :isGlutenFree")
    List<Snack> findBySnackTypeAndIsGlutenFree(@Param("snackType") SnackType snackType,
                                               @Param("isGlutenFree") Boolean isGlutenFree);

    // Find snacks by calorie range
    @Query("SELECT s FROM Snack s WHERE s.calories BETWEEN :minCalories AND :maxCalories")
    List<Snack> findByCalorieRange(@Param("minCalories") Integer minCalories,
                                   @Param("maxCalories") Integer maxCalories);
}
