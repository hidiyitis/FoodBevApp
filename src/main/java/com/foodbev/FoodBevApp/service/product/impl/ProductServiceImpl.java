package com.foodbev.FoodBevApp.service.product.impl;

import com.foodbev.FoodBevApp.dto.product.request.FoodRequest;
import com.foodbev.FoodBevApp.dto.product.response.FoodResponse;
import com.foodbev.FoodBevApp.dto.product.response.ProductListResponse;
import com.foodbev.FoodBevApp.entity.product.Coffee;
import com.foodbev.FoodBevApp.entity.product.Food;
import com.foodbev.FoodBevApp.entity.product.Product;
import com.foodbev.FoodBevApp.entity.product.Snack;
import com.foodbev.FoodBevApp.entity.product.enums.ProductCategory;
import com.foodbev.FoodBevApp.entity.product.enums.ProductStatus;
import com.foodbev.FoodBevApp.repository.product.FoodRepository;
import com.foodbev.FoodBevApp.repository.product.ProductRepository;
import com.foodbev.FoodBevApp.service.product.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.NumberFormat;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductServiceImpl implements ProductService {

    private final FoodRepository foodRepository;
    private final ProductRepository productRepository;

    @Override
    public FoodResponse createFood(FoodRequest request) {
        log.info("Creating food product with name: {}", request.getName());

        Food food = new Food();
        food.setName(request.getName());
        food.setDescription(request.getDescription());
        food.setBasePrice(request.getBasePrice());
        food.setStatus(request.getStatus());
        food.setImageUrl(request.getImageUrl());
        food.setCategory(ProductCategory.FOOD);
        food.setFoodType(request.getFoodType());
        food.setCuisineType(request.getCuisineType());
        food.setPreparationTime(request.getPreparationTime());
        food.setIsVegetarian(request.getIsVegetarian());

        Food savedFood = foodRepository.save(food);
        log.info("Food product created successfully with ID: {}", savedFood.getId());

        return mapToFoodResponse(savedFood);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductListResponse> getAllProducts(ProductCategory category, ProductStatus status, Pageable pageable) {
        log.info("Fetching products - Category: {}, Status: {}, Page: {}, Size: {}",
                category, status, pageable.getPageNumber(), pageable.getPageSize());

        Page<Product> productPage;

        // Apply filters with pagination
        if (category != null && status != null) {
            productPage = productRepository.findByCategoryAndStatus(category, status, pageable);
        } else if (category != null) {
            productPage = productRepository.findByCategory(category, pageable);
        } else if (status != null) {
            productPage = productRepository.findByStatus(status, pageable);
        } else {
            productPage = productRepository.findAll(pageable);
        }

        log.info("Found {} products on page {} of {}",
                productPage.getNumberOfElements(),
                productPage.getNumber() + 1,
                productPage.getTotalPages());

        return productPage.map(this::mapToProductListResponse);
    }

    private FoodResponse mapToFoodResponse(Food food) {
        FoodResponse response = new FoodResponse();

        response.setId(food.getId());
        response.setName(food.getName());
        response.setDescription(food.getDescription());
        response.setBasePrice(food.getBasePrice());
        response.setCalculatedPrice(food.calculatePrice());
        response.setCategory(food.getCategory());
        response.setStatus(food.getStatus());
        response.setImageUrl(food.getImageUrl());
        response.setCreatedAt(food.getCreatedAt());
        response.setUpdatedAt(food.getUpdatedAt());

        response.setFoodType(food.getFoodType());
        response.setCuisineType(food.getCuisineType());
        response.setPreparationTime(food.getPreparationTime());
        response.setIsVegetarian(food.getIsVegetarian());

        response.setPreparationTimeDisplay(food.getPreparationTime() + " minutes");
        response.setVegetarianBadge(food.getIsVegetarian() ? "Vegetarian" : "Non-Vegetarian");

        return response;
    }

    private ProductListResponse mapToProductListResponse(Product product) {
        return ProductListResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .basePrice(product.getBasePrice())
                .calculatedPrice(product.calculatePrice())
                .category(product.getCategory())
                .status(product.getStatus())
                .imageUrl(product.getImageUrl())
                .categoryDisplay(formatCategoryDisplay(product))
                .statusBadge(formatStatusBadge(product.getStatus()))
                .priceFormatted(formatPrice(product.calculatePrice()))
                .build();
    }

    private String formatCategoryDisplay(Product product) {
        String baseCategory = product.getCategory().toString();

        if (product instanceof Coffee) {
            Coffee coffee = (Coffee) product;
            return String.format("%s (%s - %s)",
                    baseCategory,
                    coffee.getCoffeeType(),
                    coffee.getSize());
        } else if (product instanceof Food) {
            Food food = (Food) product;
            return String.format("%s (%s)",
                    baseCategory,
                    food.getFoodType());
        } else if (product instanceof Snack) {
            Snack snack = (Snack) product;
            return String.format("%s (%s)",
                    baseCategory,
                    snack.getSnackType());
        }

        return baseCategory;
    }

    private String formatStatusBadge(ProductStatus status) {
        return status == ProductStatus.IN_STOCK
                ? "In Stock"
                : "Out of Stock";
    }

    private String formatPrice(java.math.BigDecimal price) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        return formatter.format(price);
    }
    
    @Override
    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }
}
