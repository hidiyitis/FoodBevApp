package com.foodbev.FoodBevApp.repository.product;

import com.foodbev.FoodBevApp.entity.product.Coffee;
import com.foodbev.FoodBevApp.entity.product.enums.CoffeeType;
import com.foodbev.FoodBevApp.entity.product.enums.ProductSize;
import com.foodbev.FoodBevApp.entity.product.enums.ProductStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CoffeeRepository extends JpaRepository<Coffee, Long> {

    // Find by coffee type
    List<Coffee> findByCoffeeType(CoffeeType coffeeType);

    // Find by size
    List<Coffee> findBySize(ProductSize size);

    // Find hot or iced coffee
    List<Coffee> findByIsHot(Boolean isHot);

    // Find by coffee type and status
    List<Coffee> findByCoffeeTypeAndStatus(CoffeeType coffeeType, ProductStatus status);

    // Find available hot coffees
    @Query("SELECT c FROM Coffee c WHERE c.isHot = true AND c.status = 'IN_STOCK'")
    List<Coffee> findAvailableHotCoffees();

    // Find available iced coffees
    @Query("SELECT c FROM Coffee c WHERE c.isHot = false AND c.status = 'IN_STOCK'")
    List<Coffee> findAvailableIcedCoffees();

    // Find by espresso shots
    List<Coffee> findByEspressoShotsGreaterThanEqual(Integer shots);

    // Find coffee by type, size and temperature
    @Query("SELECT c FROM Coffee c WHERE c.coffeeType = :type AND c.size = :size AND c.isHot = :isHot")
    List<Coffee> findByCoffeeTypeAndSizeAndIsHot(@Param("type") CoffeeType type,
                                                 @Param("size") ProductSize size,
                                                 @Param("isHot") Boolean isHot);
}

