package com.foodbev.FoodBevApp.service.product.impl;

import com.foodbev.FoodBevApp.dto.product.request.CoffeeRequest;
import com.foodbev.FoodBevApp.dto.product.request.FoodRequest;
import com.foodbev.FoodBevApp.dto.product.request.SnackRequest;
import com.foodbev.FoodBevApp.dto.product.response.CoffeeResponse;
import com.foodbev.FoodBevApp.dto.product.response.FoodResponse;
import com.foodbev.FoodBevApp.dto.product.response.ProductListResponse;
import com.foodbev.FoodBevApp.dto.product.response.SnackResponse;
import com.foodbev.FoodBevApp.entity.product.Coffee;
import com.foodbev.FoodBevApp.entity.product.Food;
import com.foodbev.FoodBevApp.entity.product.Product;
import com.foodbev.FoodBevApp.entity.product.Snack;
import com.foodbev.FoodBevApp.entity.product.enums.ProductCategory;
import com.foodbev.FoodBevApp.entity.product.enums.ProductStatus;
import com.foodbev.FoodBevApp.repository.product.CoffeeRepository;
import com.foodbev.FoodBevApp.repository.product.FoodRepository;
import com.foodbev.FoodBevApp.repository.product.ProductRepository;
import com.foodbev.FoodBevApp.repository.product.SnackRepository;
import com.foodbev.FoodBevApp.service.product.ProductService;
import com.foodbev.FoodBevApp.service.storage.FileStorageService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.text.NumberFormat;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductServiceImpl implements ProductService {

    private final FoodRepository foodRepository;
    private final SnackRepository snackRepository;
    private final CoffeeRepository coffeeRepository;
    private final ProductRepository productRepository;
    private final FileStorageService fileStorageService;

    private static final String FOOD_FOLDER = "products/food";
    private static final String SNACK_FOLDER = "products/snack";
    private static final String COFFEE_FOLDER = "products/coffee";

    @Override
    public FoodResponse createFood(FoodRequest request, MultipartFile image) {
        log.info("Creating food product with name: {}", request.getName());

        Food food = new Food();
        food.setName(request.getName());
        food.setDescription(request.getDescription());
        food.setBasePrice(request.getBasePrice());
        food.setStatus(request.getStatus());
        food.setCategory(ProductCategory.FOOD);
        food.setFoodType(request.getFoodType());
        food.setCuisineType(request.getCuisineType());
        food.setPreparationTime(request.getPreparationTime());
        food.setIsVegetarian(request.getIsVegetarian());

        // Handle image: upload file if provided, otherwise use URL from request
        if (image != null && !image.isEmpty()) {
            String imageUrl = fileStorageService.uploadFile(image, FOOD_FOLDER);
            food.setImageUrl(imageUrl);
        } else if (request.getImageUrl() != null) {
            food.setImageUrl(request.getImageUrl());
        }

        Food savedFood = foodRepository.save(food);
        log.info("Food product created successfully with ID: {}", savedFood.getId());

        return mapToFoodResponse(savedFood);
    }

    @Override
    public SnackResponse createSnack(SnackRequest request, MultipartFile image) {
        log.info("Creating snack product with name: {}", request.getName());

        Snack snack = new Snack();
        snack.setName(request.getName());
        snack.setDescription(request.getDescription());
        snack.setBasePrice(request.getBasePrice());
        snack.setStatus(request.getStatus());
        snack.setCategory(ProductCategory.SNACK);
        snack.setSnackType(request.getSnackType());
        snack.setCalories(request.getCalories());
        snack.setIsGlutenFree(request.getIsGlutenFree());

        if (image != null && !image.isEmpty()) {
            String imageUrl = fileStorageService.uploadFile(image, SNACK_FOLDER);
            snack.setImageUrl(imageUrl);
        } else if (request.getImageUrl() != null) {
            snack.setImageUrl(request.getImageUrl());
        }

        Snack savedSnack = snackRepository.save(snack);
        log.info("Snack product created successfully with ID: {}", savedSnack.getId());

        return mapToSnackResponse(savedSnack);
    }

    @Override
    public CoffeeResponse createCoffee(CoffeeRequest request, MultipartFile image) {
        log.info("Creating coffee product with name: {}", request.getName());

        Coffee coffee = new Coffee();
        coffee.setName(request.getName());
        coffee.setDescription(request.getDescription());
        coffee.setBasePrice(request.getBasePrice());
        coffee.setStatus(request.getStatus());
        coffee.setCategory(ProductCategory.COFFEE);
        coffee.setCoffeeType(request.getCoffeeType());
        coffee.setSize(request.getSize());
        coffee.setIsHot(request.getIsHot());
        coffee.setEspressoShots(request.getEspressoShots());

        if (image != null && !image.isEmpty()) {
            String imageUrl = fileStorageService.uploadFile(image, COFFEE_FOLDER);
            coffee.setImageUrl(imageUrl);
        } else if (request.getImageUrl() != null) {
            coffee.setImageUrl(request.getImageUrl());
        }

        Coffee savedCoffee = coffeeRepository.save(coffee);
        log.info("Coffee product created successfully with ID: {}", savedCoffee.getId());

        return mapToCoffeeResponse(savedCoffee);
    }

    @Override
    public String updateProductImage(Long productId, MultipartFile image) {
        log.info("Updating image for product ID: {}", productId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with ID: " + productId));

        if (product.getImageUrl() != null) {
            fileStorageService.deleteFile(product.getImageUrl());
        }

        String folder = determineFolder(product);
        String imageUrl = fileStorageService.uploadFile(image, folder);
        product.setImageUrl(imageUrl);
        productRepository.save(product);

        log.info("Image updated successfully for product ID: {}", productId);
        return imageUrl;
    }

    private String determineFolder(Product product) {
        if (product instanceof Coffee) return COFFEE_FOLDER;
        if (product instanceof Food) return FOOD_FOLDER;
        if (product instanceof Snack) return SNACK_FOLDER;
        return "products/other";
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductListResponse> getAllProducts(ProductCategory category, ProductStatus status, Pageable pageable) {
        log.info("Fetching products - Category: {}, Status: {}", category, status);

        Page<Product> productPage;

        if (category != null && status != null) {
            productPage = productRepository.findByCategoryAndStatus(category, status, pageable);
        } else if (category != null) {
            productPage = productRepository.findByCategory(category, pageable);
        } else if (status != null) {
            productPage = productRepository.findByStatus(status, pageable);
        } else {
            productPage = productRepository.findAll(pageable);
        }

        return productPage.map(this::mapToProductListResponse);
    }

    @Override
    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));
    }

    // ==================== MAPPERS ====================
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

    private SnackResponse mapToSnackResponse(Snack snack) {
        SnackResponse response = new SnackResponse();
        response.setId(snack.getId());
        response.setName(snack.getName());
        response.setDescription(snack.getDescription());
        response.setBasePrice(snack.getBasePrice());
        response.setCalculatedPrice(snack.calculatePrice());
        response.setCategory(snack.getCategory());
        response.setStatus(snack.getStatus());
        response.setImageUrl(snack.getImageUrl());
        response.setCreatedAt(snack.getCreatedAt());
        response.setUpdatedAt(snack.getUpdatedAt());
        response.setSnackType(snack.getSnackType());
        response.setCalories(snack.getCalories());
        response.setIsGlutenFree(snack.getIsGlutenFree());
        response.setGlutenFreeBadge(snack.getIsGlutenFree() ? "Gluten Free" : "Contains Gluten");
        response.setCaloriesDisplay(snack.getCalories() != null ? snack.getCalories() + " cal" : "N/A");
        return response;
    }

    private CoffeeResponse mapToCoffeeResponse(Coffee coffee) {
        CoffeeResponse response = new CoffeeResponse();
        response.setId(coffee.getId());
        response.setName(coffee.getName());
        response.setDescription(coffee.getDescription());
        response.setBasePrice(coffee.getBasePrice());
        response.setCalculatedPrice(coffee.calculatePrice());
        response.setCategory(coffee.getCategory());
        response.setStatus(coffee.getStatus());
        response.setImageUrl(coffee.getImageUrl());
        response.setCreatedAt(coffee.getCreatedAt());
        response.setUpdatedAt(coffee.getUpdatedAt());
        response.setCoffeeType(coffee.getCoffeeType());
        response.setSize(coffee.getSize());
        response.setIsHot(coffee.getIsHot());
        response.setEspressoShots(coffee.getEspressoShots());
        response.setTemperatureDisplay(coffee.getIsHot() ? "Hot" : "Iced");
        response.setSizeDisplay(coffee.getSize().toString());
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
        if (product instanceof Coffee coffee) {
            return String.format("%s (%s - %s)", baseCategory, coffee.getCoffeeType(), coffee.getSize());
        } else if (product instanceof Food food) {
            return String.format("%s (%s)", baseCategory, food.getFoodType());
        } else if (product instanceof Snack snack) {
            return String.format("%s (%s)", baseCategory, snack.getSnackType());
        }
        return baseCategory;
    }

    private String formatStatusBadge(ProductStatus status) {
        return status == ProductStatus.IN_STOCK ? "In Stock" : "Out of Stock";
    }

    private String formatPrice(java.math.BigDecimal price) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        return formatter.format(price);
    }
}
