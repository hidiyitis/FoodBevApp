package com.foodbev.FoodBevApp.service.product;

import com.foodbev.FoodBevApp.dto.product.request.CoffeeRequest;
import com.foodbev.FoodBevApp.dto.product.request.FoodRequest;
import com.foodbev.FoodBevApp.dto.product.request.SnackRequest;
import com.foodbev.FoodBevApp.dto.product.response.CoffeeResponse;
import com.foodbev.FoodBevApp.dto.product.response.FoodResponse;
import com.foodbev.FoodBevApp.dto.product.response.ProductListResponse;
import com.foodbev.FoodBevApp.dto.product.response.SnackResponse;
import com.foodbev.FoodBevApp.entity.product.Product;
import com.foodbev.FoodBevApp.entity.product.enums.ProductCategory;
import com.foodbev.FoodBevApp.entity.product.enums.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface ProductService {
    FoodResponse createFood(FoodRequest request, MultipartFile image);
    SnackResponse createSnack(SnackRequest request, MultipartFile image);
    CoffeeResponse createCoffee(CoffeeRequest request, MultipartFile image);
    String updateProductImage(Long productId, MultipartFile image);
    Page<ProductListResponse> getAllProducts(ProductCategory category, ProductStatus status, Pageable pageable);
    Product findById(Long id);
}
