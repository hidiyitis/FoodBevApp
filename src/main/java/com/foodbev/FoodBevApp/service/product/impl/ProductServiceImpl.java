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
import com.foodbev.FoodBevApp.util.ProductFormatUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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

    // ======================== CREATE ========================

    // 1. log info nama produk yang akan dibuat
    // 2. buat instance entity sesuai tipe produk (Food, Snack, Coffee)
    // 3. set field-field entity dengan data dari request
    // 4. jika ada file image, upload file dan set imageUrl, jika tidak ada file tapi ada imageUrl di request, set imageUrl dari request
    // 5. simpan entity ke database via repository method save (dari jpa repository)
    // 6. log info ID produk yang berhasil dibuat
    // 7. kembalikan response yang sudah di-map dari entity (mapToFoodResponse)
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

    // 1. log info nama produk yang akan dibuat
    // 2. buat instance entity sesuai tipe produk (Food, Snack, Coffee)
    // 3. set field-field entity dengan data dari request
    // 4. jika ada file image, upload file dan set imageUrl, jika tidak ada file tapi ada imageUrl di request, set imageUrl dari request
    // 5. simpan entity ke database via repository method save (dari jpa repository)
    // 6. log info ID produk yang berhasil dibuat
    // 7. kembalikan response yang sudah di-map dari entity (mapToSnackResponse)
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

    // 1. log info nama produk yang akan dibuat
    // 2. buat instance entity sesuai tipe produk (Food, Snack, Coffee)
    // 3. set field-field entity dengan data dari request
    // 4. jika ada file image, upload file dan set imageUrl, jika tidak ada file tapi ada imageUrl di request, set imageUrl dari request
    // 5. simpan entity ke database via repository method save (dari jpa repository)
    // 6. log info ID produk yang berhasil dibuat
    // 7. kembalikan response yang sudah di-map dari entity (mapToCoffeeResponse)
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

    // ======================== UPDATE ========================

    // 1. log info ID produk yang akan diupdate
    // 2. cari produk berdasarkan ID, jika tidak ditemukan lempar EntityNotFoundException
    // 3. update field-field produk dengan data dari request
    // 4. jika ada file image baru, hapus file lama (jika ada img url yang lama) lalu upload dan set file baru
    // 5. simpan perubahan produk ke database
    // 6. kembalikan response yang sudah di-map dari entity (mapToFoodResponse)
    @Override
    public FoodResponse updateFood(Long id, FoodRequest request, MultipartFile image) {
        log.info("Updating food product with ID: {}", id);

        Food food = foodRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Food not found with ID: " + id));

        food.setName(request.getName());
        food.setDescription(request.getDescription());
        food.setBasePrice(request.getBasePrice());
        food.setStatus(request.getStatus());
        food.setFoodType(request.getFoodType());
        food.setCuisineType(request.getCuisineType());
        food.setPreparationTime(request.getPreparationTime());
        food.setIsVegetarian(request.getIsVegetarian() != null ? request.getIsVegetarian() : false);

        if (image != null && !image.isEmpty()) {
            if (food.getImageUrl() != null) {
                fileStorageService.deleteFile(food.getImageUrl());
            }
            String imageUrl = fileStorageService.uploadFile(image, FOOD_FOLDER);
            food.setImageUrl(imageUrl);
        }

        Food savedFood = foodRepository.save(food);
        return mapToFoodResponse(savedFood);
    }

    // 1. log info ID produk yang akan diupdate
    // 2. cari produk berdasarkan ID, jika tidak ditemukan lempar EntityNotFoundException
    // 3. update field-field produk dengan data dari request
    // 4. jika ada file image baru, hapus file lama (jika ada img url yang lama) lalu upload dan set file baru
    // 5. simpan perubahan produk ke database
    // 6. kembalikan response yang sudah di-map dari entity (mapToSnackResponse)
    @Override
    public SnackResponse updateSnack(Long id, SnackRequest request, MultipartFile image) {
        log.info("Updating snack product with ID: {}", id);

        Snack snack = snackRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Snack not found with ID: " + id));

        snack.setName(request.getName());
        snack.setDescription(request.getDescription());
        snack.setBasePrice(request.getBasePrice());
        snack.setStatus(request.getStatus());
        snack.setSnackType(request.getSnackType());
        snack.setCalories(request.getCalories());
        snack.setIsGlutenFree(request.getIsGlutenFree());

        if (image != null && !image.isEmpty()) {
            if (snack.getImageUrl() != null) {
                fileStorageService.deleteFile(snack.getImageUrl());
            }
            String imageUrl = fileStorageService.uploadFile(image, SNACK_FOLDER);
            snack.setImageUrl(imageUrl);
        }

        Snack savedSnack = snackRepository.save(snack);
        return mapToSnackResponse(savedSnack);
    }

    // 1. log info ID produk yang akan diupdate
    // 2. cari produk berdasarkan ID, jika tidak ditemukan lempar EntityNotFoundException
    // 3. update field-field produk dengan data dari request
    // 4. jika ada file image baru, hapus file lama (jika ada img url yang lama) lalu upload dan set file baru
    // 5. simpan perubahan produk ke database
    // 6. kembalikan response yang sudah di-map dari entity (mapToCoffeeResponse)
    @Override
    public CoffeeResponse updateCoffee(Long id, CoffeeRequest request, MultipartFile image) {
        log.info("Updating coffee product with ID: {}", id);

        Coffee coffee = coffeeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Coffee not found with ID: " + id));

        coffee.setName(request.getName());
        coffee.setDescription(request.getDescription());
        coffee.setBasePrice(request.getBasePrice());
        coffee.setStatus(request.getStatus());
        coffee.setCoffeeType(request.getCoffeeType());
        coffee.setSize(request.getSize());
        coffee.setIsHot(request.getIsHot());
        coffee.setEspressoShots(request.getEspressoShots());

        if (image != null && !image.isEmpty()) {
            if (coffee.getImageUrl() != null) {
                fileStorageService.deleteFile(coffee.getImageUrl());
            }
            String imageUrl = fileStorageService.uploadFile(image, COFFEE_FOLDER);
            coffee.setImageUrl(imageUrl);
        }

        Coffee savedCoffee = coffeeRepository.save(coffee);
        return mapToCoffeeResponse(savedCoffee);
    }

    // ======================== READ ========================

    // 1. log info category, status, dan pageable untuk pagination
    // 2. buat varible productPage untuk menampung hasil query
    // 3. Jika kategori dan status ada, ambil produk via findByCategoryAndStatus.
    // 4. Jika hanya kategori ada, ambil produk via findByCategory.
    // 5. Jika hanya status ada, ambil produk via findByStatus.
    // 6. Jika keduanya kosong, ambil semua produk via findAll.
    // 7. Ubah hasil Page<Product> menjadi Page<ProductListResponse> lewat map (mapToProductListResponse)
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

    // 1. log info keyword, category, status, dan pageable untuk pagination
    // 2. buat varible productPage untuk menampung hasil query
    // 3. Jika keyword kosong, panggil getAllProducts sebagai fallback
    // 4. Jika kategori dan status ada, ambil produk via searchByKeywordAndCategoryAndStatus.
    // 5. Jika hanya kategori ada, ambil produk via searchByKeywordAndCategory.
    // 6. Jika hanya status ada, ambil produk via searchByKeywordAndStatus.
    // 7. Jika keduanya kosong, ambil produk via searchByKeyword.
    // 8. Ubah hasil Page<Product> menjadi Page<ProductListResponse> lewat map (mapToProductListResponse)
    @Override
    @Transactional(readOnly = true)
    public Page<ProductListResponse> searchProducts(String keyword, ProductCategory category, ProductStatus status, Pageable pageable) {
        log.info("Searching products - Keyword: {}, Category: {}, Status: {}", keyword, category, status);

        Page<Product> productPage;

        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllProducts(category, status, pageable);
        }

        String searchKeyword = keyword.trim();

        if (category != null && status != null) {
            productPage = productRepository.searchByKeywordAndCategoryAndStatus(searchKeyword, category, status, pageable);
        } else if (category != null) {
            productPage = productRepository.searchByKeywordAndCategory(searchKeyword, category, pageable);
        } else if (status != null) {
            productPage = productRepository.searchByKeywordAndStatus(searchKeyword, status, pageable);
        } else {
            productPage = productRepository.searchByKeyword(searchKeyword, pageable);
        }

        return productPage.map(this::mapToProductListResponse);
    }

    // 1. Mencari produk berdasarkan ID menggunakan productRepository dengan method findById.
    // 2. Jika produk dengan ID tersebut tidak ditemukan, melempar EntityNotFoundException dengan pesan "Product not found".
    // 3. Jika produk ditemukan, mengembalikan objek Product yang ditemukan.
    @Override
    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));
    }

    // ======================== DELETE ========================

    // 1. Mencari produk berdasarkan ID menggunakan productRepository dengan method findById.
    // 2. Jika produk dengan ID tersebut tidak ditemukan, melempar EntityNotFoundException dengan pesan "Product not found with ID: " diikuti ID produk.
    // 3. Jika produk ditemukan, mengubah status produk menjadi DISCONTINUED (soft-delete).
    // 4. Menyimpan perubahan produk ke database menggunakan productRepository.
    @Override
    public void deleteProduct(Long id) {
        log.info("Deleting product with ID: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with ID: " + id));


        product.setStatus(ProductStatus.DISCONTINUED);
        productRepository.save(product);

        log.info("Product soft-deleted (set to DISCONTINUED) with ID: {}", id);
    }

    // ==================== MAPPER HELPERS ====================
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
                .categoryDisplay(ProductFormatUtils.formatCategoryDisplay(product))
                .statusBadge(ProductFormatUtils.formatStatusBadge(product.getStatus()))
                .priceFormatted(ProductFormatUtils.formatPrice(product.calculatePrice()))
                .build();
    }
}
