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


}
