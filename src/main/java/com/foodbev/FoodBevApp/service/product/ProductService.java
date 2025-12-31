package com.foodbev.FoodBevApp.service.product;

import com.foodbev.FoodBevApp.dto.product.request.FoodRequest;
import com.foodbev.FoodBevApp.dto.product.response.FoodResponse;
import com.foodbev.FoodBevApp.dto.product.response.ProductListResponse;
import com.foodbev.FoodBevApp.entity.product.Product;
import com.foodbev.FoodBevApp.entity.product.enums.ProductCategory;
import com.foodbev.FoodBevApp.entity.product.enums.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {

    Product findById(Long id);

    FoodResponse createFood(FoodRequest request);

    Page<ProductListResponse> getAllProducts(ProductCategory category, ProductStatus status, Pageable pageable);
}
