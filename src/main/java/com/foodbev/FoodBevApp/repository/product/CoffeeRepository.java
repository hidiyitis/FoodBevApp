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
}

