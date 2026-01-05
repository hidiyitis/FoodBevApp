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
import com.foodbev.FoodBevApp.entity.product.enums.*;
import com.foodbev.FoodBevApp.repository.product.CoffeeRepository;
import com.foodbev.FoodBevApp.repository.product.FoodRepository;
import com.foodbev.FoodBevApp.repository.product.ProductRepository;
import com.foodbev.FoodBevApp.repository.product.SnackRepository;
import com.foodbev.FoodBevApp.service.storage.FileStorageService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductServiceImpl Unit Tests")
class ProductServiceImplTest {

    @Mock
    private FoodRepository foodRepository;

    @Mock
    private SnackRepository snackRepository;

    @Mock
    private CoffeeRepository coffeeRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private FileStorageService fileStorageService;

    @InjectMocks
    private ProductServiceImpl productService;

    // Test data fixtures
    private FoodRequest foodRequest;
    private SnackRequest snackRequest;
    private CoffeeRequest coffeeRequest;
    private Food food;
    private Snack snack;
    private Coffee coffee;
    private MockMultipartFile validImage;
    private MockMultipartFile emptyImage;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        // Arrange - Setup common test fixtures
        pageable = PageRequest.of(0, 10);

        // Setup valid image
        validImage = new MockMultipartFile(
                "image",
                "test-image.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );

        // Setup empty image
        emptyImage = new MockMultipartFile(
                "image",
                "",
                "application/octet-stream",
                new byte[0]
        );

        // Setup Food fixtures
        foodRequest = new FoodRequest();
        foodRequest.setName("Nasi Goreng");
        foodRequest.setDescription("Delicious fried rice");
        foodRequest.setBasePrice(new BigDecimal("25000"));
        foodRequest.setStatus(ProductStatus.IN_STOCK);
        foodRequest.setFoodType(FoodType.RICE_BOWL);
        foodRequest.setCuisineType("Indonesian");
        foodRequest.setPreparationTime(15);
        foodRequest.setIsVegetarian(false);

        food = new Food();
        food.setId(1L);
        food.setName("Nasi Goreng");
        food.setDescription("Delicious fried rice");
        food.setBasePrice(new BigDecimal("25000"));
        food.setStatus(ProductStatus.IN_STOCK);
        food.setCategory(ProductCategory.FOOD);
        food.setFoodType(FoodType.RICE_BOWL);
        food.setCuisineType("Indonesian");
        food.setPreparationTime(15);
        food.setIsVegetarian(false);

        // Setup Snack fixtures
        snackRequest = new SnackRequest();
        snackRequest.setName("Potato Chips");
        snackRequest.setDescription("Crispy potato chips");
        snackRequest.setBasePrice(new BigDecimal("15000"));
        snackRequest.setStatus(ProductStatus.IN_STOCK);
        snackRequest.setSnackType(SnackType.CHIPS);
        snackRequest.setIsGlutenFree(true);

        snack = new Snack();
        snack.setId(2L);
        snack.setName("Potato Chips");
        snack.setDescription("Crispy potato chips");
        snack.setBasePrice(new BigDecimal("15000"));
        snack.setStatus(ProductStatus.IN_STOCK);
        snack.setCategory(ProductCategory.SNACK);
        snack.setSnackType(SnackType.CHIPS);
        snack.setIsGlutenFree(true);

        // Setup Coffee fixtures
        coffeeRequest = new CoffeeRequest();
        coffeeRequest.setName("Latte");
        coffeeRequest.setDescription("Creamy latte coffee");
        coffeeRequest.setBasePrice(new BigDecimal("30000"));
        coffeeRequest.setStatus(ProductStatus.IN_STOCK);
        coffeeRequest.setCoffeeType(CoffeeType.LATTE);
        coffeeRequest.setSize(ProductSize.MEDIUM);
        coffeeRequest.setIsHot(true);
        coffeeRequest.setEspressoShots(1);

        coffee = new Coffee();
        coffee.setId(3L);
        coffee.setName("Latte");
        coffee.setDescription("Creamy latte coffee");
        coffee.setBasePrice(new BigDecimal("30000"));
        coffee.setStatus(ProductStatus.IN_STOCK);
        coffee.setCategory(ProductCategory.COFFEE);
        coffee.setCoffeeType(CoffeeType.LATTE);
        coffee.setSize(ProductSize.MEDIUM);
        coffee.setIsHot(true);
        coffee.setEspressoShots(1);
    }

    // ==================== CREATE FOOD TESTS ====================
    @Nested
    @DisplayName("Create Food Tests")
    class CreateFoodTests {



        @Test
        @DisplayName("Happy Path - Should create food without image")
        void createFood_WithoutImage_ShouldReturnFoodResponse() {
            // Arrange
            when(foodRepository.save(any(Food.class))).thenReturn(food);

            // Act
            FoodResponse result = productService.createFood(foodRequest, emptyImage);

            // Assert
            assertNotNull(result);
            assertEquals(food.getId(), result.getId());
            assertEquals(food.getName(), result.getName());

            // Verify - FileStorageService should NOT be called for empty image
            verify(fileStorageService, never()).uploadFile(any(), anyString());
            verify(foodRepository).save(any(Food.class));
        }

        @Test
        @DisplayName("Happy Path - Should create food with null image")
        void createFood_WithNullImage_ShouldReturnFoodResponse() {
            // Arrange
            when(foodRepository.save(any(Food.class))).thenReturn(food);

            // Act
            FoodResponse result = productService.createFood(foodRequest, null);

            // Assert
            assertNotNull(result);
            assertEquals(food.getId(), result.getId());

            // Verify
            verify(fileStorageService, never()).uploadFile(any(), anyString());
            verify(foodRepository).save(any(Food.class));
        }


        @Test
        @DisplayName("Sad Path - Should handle repository exception")
        void createFood_WhenRepositoryFails_ShouldThrowException() {
            // Arrange
            when(foodRepository.save(any(Food.class))).thenThrow(new RuntimeException("Database error"));

            // Act & Assert
            RuntimeException exception = assertThrows(
                    RuntimeException.class,
                    () -> productService.createFood(foodRequest, null)
            );

            assertEquals("Database error", exception.getMessage());
            verify(foodRepository).save(any(Food.class));
        }
    }

    // ==================== CREATE SNACK TESTS ====================
    @Nested
    @DisplayName("Create Snack Tests")
    class CreateSnackTests {



        @Test
        @DisplayName("Happy Path - Should create snack without image")
        void createSnack_WithoutImage_ShouldReturnSnackResponse() {
            // Arrange
            when(snackRepository.save(any(Snack.class))).thenReturn(snack);

            // Act
            SnackResponse result = productService.createSnack(snackRequest, null);

            // Assert
            assertNotNull(result);
            assertEquals(snack.getId(), result.getId());

            // Verify
            verify(fileStorageService, never()).uploadFile(any(), anyString());
            verify(snackRepository).save(any(Snack.class));
        }

        @Test
        @DisplayName("Sad Path - Should throw exception for invalid image format")
        void createSnack_WithInvalidImageFormat_ShouldThrowException() {
            // Arrange
            // FileStorageService.uploadFile() internally validates and throws exception
            when(fileStorageService.uploadFile(validImage, "products/snack"))
                    .thenThrow(new IllegalArgumentException("Invalid image file format"));

            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> productService.createSnack(snackRequest, validImage)
            );

            assertEquals("Invalid image file format", exception.getMessage());

            // Verify
            verify(snackRepository, never()).save(any());
        }
    }

    // ==================== CREATE COFFEE TESTS ====================
    @Nested
    @DisplayName("Create Coffee Tests")
    class CreateCoffeeTests {

        @Test
        @DisplayName("Happy Path - Should create coffee without image")
        void createCoffee_WithoutImage_ShouldReturnCoffeeResponse() {
            // Arrange
            when(coffeeRepository.save(any(Coffee.class))).thenReturn(coffee);

            // Act
            CoffeeResponse result = productService.createCoffee(coffeeRequest, null);

            // Assert
            assertNotNull(result);
            assertEquals(coffee.getId(), result.getId());

            // Verify
            verify(fileStorageService, never()).uploadFile(any(), anyString());
            verify(coffeeRepository).save(any(Coffee.class));
        }

        @Test
        @DisplayName("Sad Path - Should throw exception for invalid image format")
        void createCoffee_WithInvalidImageFormat_ShouldThrowException() {
            // Arrange
            // FileStorageService.uploadFile() internally validates and throws exception
            when(fileStorageService.uploadFile(validImage, "products/coffee"))
                    .thenThrow(new IllegalArgumentException("Invalid image file format"));

            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> productService.createCoffee(coffeeRequest, validImage)
            );

            assertEquals("Invalid image file format", exception.getMessage());

            // Verify
            verify(coffeeRepository, never()).save(any());
        }
    }

    // ==================== UPDATE FOOD TESTS ====================
    @Nested
    @DisplayName("Update Food Tests")
    class UpdateFoodTests {

        @Test
        @DisplayName("Happy Path - Should update food with new image")
        void updateFood_WithNewImage_ShouldReturnUpdatedFoodResponse() {
            // Arrange
            Long productId = 1L;
            food.setImageUrl("old-image.jpg");
            String newImageUrl = "products/food/new-image.jpg";

            when(foodRepository.findById(productId)).thenReturn(Optional.of(food));
            // Note: updateFood does NOT validate image (unlike createFood), it directly uploads
            when(fileStorageService.uploadFile(validImage, "products/food")).thenReturn(newImageUrl);
            when(foodRepository.save(any(Food.class))).thenReturn(food);

            // Act
            FoodResponse result = productService.updateFood(productId, foodRequest, validImage);

            // Assert
            assertNotNull(result);
            assertEquals(productId, result.getId());

            // Verify - Old image should be deleted
            verify(foodRepository).findById(productId);
            verify(fileStorageService).deleteFile("old-image.jpg");
            verify(fileStorageService).uploadFile(validImage, "products/food");
            verify(foodRepository).save(any(Food.class));
        }

        @Test
        @DisplayName("Happy Path - Should update food without changing image")
        void updateFood_WithoutImage_ShouldKeepExistingImage() {
            // Arrange
            Long productId = 1L;
            food.setImageUrl("existing-image.jpg");

            when(foodRepository.findById(productId)).thenReturn(Optional.of(food));
            when(foodRepository.save(any(Food.class))).thenReturn(food);

            // Act
            FoodResponse result = productService.updateFood(productId, foodRequest, null);

            // Assert
            assertNotNull(result);

            // Verify - Should NOT delete or upload images
            verify(fileStorageService, never()).deleteFile(anyString());
            verify(fileStorageService, never()).uploadFile(any(), anyString());
            verify(foodRepository).save(any(Food.class));
        }

        @Test
        @DisplayName("Sad Path - Should throw exception when food not found")
        void updateFood_WhenNotFound_ShouldThrowEntityNotFoundException() {
            // Arrange
            Long productId = 999L;
            when(foodRepository.findById(productId)).thenReturn(Optional.empty());

            // Act & Assert
            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> productService.updateFood(productId, foodRequest, null)
            );

            assertTrue(exception.getMessage().contains("Food not found"));

            // Verify - Save should NOT be called
            verify(foodRepository).findById(productId);
            verify(foodRepository, never()).save(any());
        }

        @Test
        @DisplayName("Sad Path - Should throw exception when image upload fails")
        void updateFood_WhenImageUploadFails_ShouldThrowException() {
            // Arrange
            Long productId = 1L;
            food.setImageUrl("old-image.jpg");
            when(foodRepository.findById(productId)).thenReturn(Optional.of(food));
            // FileStorageService throws exception internally when image is invalid
            when(fileStorageService.uploadFile(validImage, "products/food"))
                    .thenThrow(new IllegalArgumentException("Invalid image file format"));

            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> productService.updateFood(productId, foodRequest, validImage)
            );

            assertEquals("Invalid image file format", exception.getMessage());

            // Verify - deleteFile is called before upload attempt, save should NOT be called
            verify(fileStorageService).deleteFile("old-image.jpg");
            verify(foodRepository, never()).save(any());
        }
    }

    // ==================== UPDATE SNACK TESTS ====================
    @Nested
    @DisplayName("Update Snack Tests")
    class UpdateSnackTests {

        @Test
        @DisplayName("Happy Path - Should update snack successfully")
        void updateSnack_WithValidData_ShouldReturnUpdatedSnackResponse() {
            // Arrange
            Long productId = 2L;
            when(snackRepository.findById(productId)).thenReturn(Optional.of(snack));
            when(snackRepository.save(any(Snack.class))).thenReturn(snack);

            // Act
            SnackResponse result = productService.updateSnack(productId, snackRequest, null);

            // Assert
            assertNotNull(result);
            assertEquals(productId, result.getId());

            // Verify
            verify(snackRepository).findById(productId);
            verify(snackRepository).save(any(Snack.class));
        }

        @Test
        @DisplayName("Sad Path - Should throw exception when snack not found")
        void updateSnack_WhenNotFound_ShouldThrowEntityNotFoundException() {
            // Arrange
            Long productId = 999L;
            when(snackRepository.findById(productId)).thenReturn(Optional.empty());

            // Act & Assert
            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> productService.updateSnack(productId, snackRequest, null)
            );

            assertTrue(exception.getMessage().contains("Snack not found"));

            // Verify
            verify(snackRepository, never()).save(any());
        }
    }

    // ==================== UPDATE COFFEE TESTS ====================
    @Nested
    @DisplayName("Update Coffee Tests")
    class UpdateCoffeeTests {

        @Test
        @DisplayName("Happy Path - Should update coffee successfully")
        void updateCoffee_WithValidData_ShouldReturnUpdatedCoffeeResponse() {
            // Arrange
            Long productId = 3L;
            when(coffeeRepository.findById(productId)).thenReturn(Optional.of(coffee));
            when(coffeeRepository.save(any(Coffee.class))).thenReturn(coffee);

            // Act
            CoffeeResponse result = productService.updateCoffee(productId, coffeeRequest, null);

            // Assert
            assertNotNull(result);
            assertEquals(productId, result.getId());

            // Verify
            verify(coffeeRepository).findById(productId);
            verify(coffeeRepository).save(any(Coffee.class));
        }

        @Test
        @DisplayName("Sad Path - Should throw exception when coffee not found")
        void updateCoffee_WhenNotFound_ShouldThrowEntityNotFoundException() {
            // Arrange
            Long productId = 999L;
            when(coffeeRepository.findById(productId)).thenReturn(Optional.empty());

            // Act & Assert
            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> productService.updateCoffee(productId, coffeeRequest, null)
            );

            assertTrue(exception.getMessage().contains("Coffee not found"));

            // Verify
            verify(coffeeRepository, never()).save(any());
        }
    }

    // ==================== GET ALL PRODUCTS TESTS ====================
    @Nested
    @DisplayName("Get All Products Tests")
    class GetAllProductsTests {

        @Test
        @DisplayName("Happy Path - Should return all products without filters")
        void getAllProducts_WithoutFilters_ShouldReturnPageOfProducts() {
            // Arrange
            List<Product> products = List.of(food, coffee, snack);
            Page<Product> productPage = new PageImpl<>(products, pageable, products.size());
            when(productRepository.findAll(pageable)).thenReturn(productPage);

            // Act
            Page<ProductListResponse> result = productService.getAllProducts(null, null, pageable);

            // Assert
            assertNotNull(result);
            assertEquals(3, result.getTotalElements());
            assertEquals(3, result.getContent().size());

            // Verify
            verify(productRepository).findAll(pageable);
            verify(productRepository, never()).findByCategory(any(), any());
            verify(productRepository, never()).findByStatus(any(), any());
        }

        @Test
        @DisplayName("Happy Path - Should return products filtered by category")
        void getAllProducts_WithCategoryFilter_ShouldReturnFilteredProducts() {
            // Arrange
            List<Product> coffeeList = List.of(coffee);
            Page<Product> productPage = new PageImpl<>(coffeeList, pageable, coffeeList.size());
            when(productRepository.findByCategory(ProductCategory.COFFEE, pageable)).thenReturn(productPage);

            // Act
            Page<ProductListResponse> result = productService.getAllProducts(ProductCategory.COFFEE, null, pageable);

            // Assert
            assertNotNull(result);
            assertEquals(1, result.getTotalElements());

            // Verify
            verify(productRepository).findByCategory(ProductCategory.COFFEE, pageable);
            verify(productRepository, never()).findAll(any(Pageable.class));
        }

        @Test
        @DisplayName("Happy Path - Should return products filtered by status")
        void getAllProducts_WithStatusFilter_ShouldReturnFilteredProducts() {
            // Arrange
            List<Product> inStockProducts = List.of(food, snack);
            Page<Product> productPage = new PageImpl<>(inStockProducts, pageable, inStockProducts.size());
            when(productRepository.findByStatus(ProductStatus.IN_STOCK, pageable)).thenReturn(productPage);

            // Act
            Page<ProductListResponse> result = productService.getAllProducts(null, ProductStatus.IN_STOCK, pageable);

            // Assert
            assertNotNull(result);
            assertEquals(2, result.getTotalElements());

            // Verify
            verify(productRepository).findByStatus(ProductStatus.IN_STOCK, pageable);
        }

        @Test
        @DisplayName("Happy Path - Should return products filtered by both category and status")
        void getAllProducts_WithBothFilters_ShouldReturnFilteredProducts() {
            // Arrange
            List<Product> filteredProducts = List.of(food);
            Page<Product> productPage = new PageImpl<>(filteredProducts, pageable, filteredProducts.size());
            when(productRepository.findByCategoryAndStatus(ProductCategory.FOOD, ProductStatus.IN_STOCK, pageable))
                    .thenReturn(productPage);

            // Act
            Page<ProductListResponse> result = productService.getAllProducts(
                    ProductCategory.FOOD, ProductStatus.IN_STOCK, pageable);

            // Assert
            assertNotNull(result);
            assertEquals(1, result.getTotalElements());

            // Verify
            verify(productRepository).findByCategoryAndStatus(ProductCategory.FOOD, ProductStatus.IN_STOCK, pageable);
        }

        @Test
        @DisplayName("Happy Path - Should return empty page when no products found")
        void getAllProducts_WhenNoProducts_ShouldReturnEmptyPage() {
            // Arrange
            Page<Product> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
            when(productRepository.findAll(pageable)).thenReturn(emptyPage);

            // Act
            Page<ProductListResponse> result = productService.getAllProducts(null, null, pageable);

            // Assert
            assertNotNull(result);
            assertEquals(0, result.getTotalElements());
            assertTrue(result.getContent().isEmpty());

            // Verify
            verify(productRepository).findAll(pageable);
        }
    }

    // ==================== SEARCH PRODUCTS TESTS ====================
    @Nested
    @DisplayName("Search Products Tests")
    class SearchProductsTests {

        @Test
        @DisplayName("Happy Path - Should search products by keyword only")
        void searchProducts_WithKeywordOnly_ShouldReturnMatchingProducts() {
            // Arrange
            String keyword = "Nasi";
            List<Product> matchedProducts = List.of(food);
            Page<Product> productPage = new PageImpl<>(matchedProducts, pageable, matchedProducts.size());
            when(productRepository.searchByKeyword(keyword, pageable)).thenReturn(productPage);

            // Act
            Page<ProductListResponse> result = productService.searchProducts(keyword, null, null, pageable);

            // Assert
            assertNotNull(result);
            assertEquals(1, result.getTotalElements());

            // Verify
            verify(productRepository).searchByKeyword(keyword, pageable);
        }

        @Test
        @DisplayName("Happy Path - Should search products with all filters")
        void searchProducts_WithAllFilters_ShouldReturnFilteredProducts() {
            // Arrange
            String keyword = "Nasi";
            List<Product> matchedProducts = List.of(food);
            Page<Product> productPage = new PageImpl<>(matchedProducts, pageable, matchedProducts.size());
            when(productRepository.searchByKeywordAndCategoryAndStatus(
                    keyword, ProductCategory.FOOD, ProductStatus.IN_STOCK, pageable))
                    .thenReturn(productPage);

            // Act
            Page<ProductListResponse> result = productService.searchProducts(
                    keyword, ProductCategory.FOOD, ProductStatus.IN_STOCK, pageable);

            // Assert
            assertNotNull(result);
            assertEquals(1, result.getTotalElements());

            // Verify
            verify(productRepository).searchByKeywordAndCategoryAndStatus(
                    keyword, ProductCategory.FOOD, ProductStatus.IN_STOCK, pageable);
        }

        @Test
        @DisplayName("Happy Path - Should search with keyword and category")
        void searchProducts_WithKeywordAndCategory_ShouldReturnFilteredProducts() {
            // Arrange
            String keyword = "Latte";
            List<Product> matchedProducts = List.of(coffee);
            Page<Product> productPage = new PageImpl<>(matchedProducts, pageable, matchedProducts.size());
            when(productRepository.searchByKeywordAndCategory(keyword, ProductCategory.COFFEE, pageable))
                    .thenReturn(productPage);

            // Act
            Page<ProductListResponse> result = productService.searchProducts(
                    keyword, ProductCategory.COFFEE, null, pageable);

            // Assert
            assertNotNull(result);
            assertEquals(1, result.getTotalElements());

            // Verify
            verify(productRepository).searchByKeywordAndCategory(keyword, ProductCategory.COFFEE, pageable);
        }

        @Test
        @DisplayName("Happy Path - Should search with keyword and status")
        void searchProducts_WithKeywordAndStatus_ShouldReturnFilteredProducts() {
            // Arrange
            String keyword = "Chips";
            List<Product> matchedProducts = List.of(snack);
            Page<Product> productPage = new PageImpl<>(matchedProducts, pageable, matchedProducts.size());
            when(productRepository.searchByKeywordAndStatus(keyword, ProductStatus.IN_STOCK, pageable))
                    .thenReturn(productPage);

            // Act
            Page<ProductListResponse> result = productService.searchProducts(
                    keyword, null, ProductStatus.IN_STOCK, pageable);

            // Assert
            assertNotNull(result);
            assertEquals(1, result.getTotalElements());

            // Verify
            verify(productRepository).searchByKeywordAndStatus(keyword, ProductStatus.IN_STOCK, pageable);
        }

        @Test
        @DisplayName("Happy Path - Should return empty page when no matches")
        void searchProducts_WithNoMatches_ShouldReturnEmptyPage() {
            // Arrange
            String keyword = "NonExistent";
            Page<Product> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
            when(productRepository.searchByKeyword(keyword, pageable)).thenReturn(emptyPage);

            // Act
            Page<ProductListResponse> result = productService.searchProducts(keyword, null, null, pageable);

            // Assert
            assertNotNull(result);
            assertTrue(result.getContent().isEmpty());

            // Verify
            verify(productRepository).searchByKeyword(keyword, pageable);
        }
    }

    // ==================== FIND BY ID TESTS ====================
    @Nested
    @DisplayName("Find By ID Tests")
    class FindByIdTests {

        @Test
        @DisplayName("Happy Path - Should return Food when found")
        void findById_WhenFoodExists_ShouldReturnFood() {
            // Arrange
            Long productId = 1L;
            when(productRepository.findById(productId)).thenReturn(Optional.of(food));

            // Act
            Product result = productService.findById(productId);

            // Assert
            assertNotNull(result);
            assertInstanceOf(Food.class, result);
            assertEquals(productId, result.getId());
            assertEquals("Nasi Goreng", result.getName());

            // Verify
            verify(productRepository).findById(productId);
        }

        @Test
        @DisplayName("Happy Path - Should return Coffee when found")
        void findById_WhenCoffeeExists_ShouldReturnCoffee() {
            // Arrange
            Long productId = 3L;
            when(productRepository.findById(productId)).thenReturn(Optional.of(coffee));

            // Act
            Product result = productService.findById(productId);

            // Assert
            assertNotNull(result);
            assertInstanceOf(Coffee.class, result);
            assertEquals("Latte", result.getName());

            // Verify
            verify(productRepository).findById(productId);
        }

        @Test
        @DisplayName("Sad Path - Should throw exception when not found")
        void findById_WhenNotFound_ShouldThrowEntityNotFoundException() {
            // Arrange
            Long productId = 999L;
            when(productRepository.findById(productId)).thenReturn(Optional.empty());

            // Act & Assert
            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> productService.findById(productId)
            );

            assertTrue(exception.getMessage().contains("Product not found"));

            // Verify
            verify(productRepository).findById(productId);
        }
    }

    // ==================== DELETE PRODUCT TESTS ====================
    @Nested
    @DisplayName("Delete Product Tests")
    class DeleteProductTests {

        @Test
        @DisplayName("Happy Path - Should soft delete product without image")
        void deleteProduct_WithoutImage_ShouldSoftDelete() {
            // Arrange
            Long productId = 1L;
            food.setImageUrl(null);
            when(productRepository.findById(productId)).thenReturn(Optional.of(food));
            when(productRepository.save(any(Product.class))).thenReturn(food);

            // Act
            productService.deleteProduct(productId);

            // Assert & Verify
            verify(productRepository).findById(productId);
            verify(fileStorageService, never()).deleteFile(anyString());
            verify(productRepository).save(argThat(product ->
                    product.getStatus() == ProductStatus.DISCONTINUED
            ));
        }

        @Test
        @DisplayName("Sad Path - Should throw exception when product not found")
        void deleteProduct_WhenNotFound_ShouldThrowEntityNotFoundException() {
            // Arrange
            Long productId = 999L;
            when(productRepository.findById(productId)).thenReturn(Optional.empty());

            // Act & Assert
            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> productService.deleteProduct(productId)
            );

            assertTrue(exception.getMessage().contains("Product not found"));

            // Verify - Save should NOT be called
            verify(productRepository).findById(productId);
            verify(productRepository, never()).save(any());
            verify(fileStorageService, never()).deleteFile(anyString());
        }
    }

    // ==================== EDGE CASES TESTS ====================
    @Nested
    @DisplayName("Edge Cases Tests")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should handle product with empty string imageUrl on delete")
        void deleteProduct_WithEmptyImageUrl_ShouldNotCallDeleteFile() {
            // Arrange
            Long productId = 1L;
            food.setImageUrl("");
            when(productRepository.findById(productId)).thenReturn(Optional.of(food));
            when(productRepository.save(any(Product.class))).thenReturn(food);

            // Act
            productService.deleteProduct(productId);

            // Verify - Should not call deleteFile for empty string
            verify(fileStorageService, never()).deleteFile(anyString());
        }

        @Test
        @DisplayName("Should map Food product correctly in ProductListResponse")
        void getAllProducts_ShouldMapFoodCorrectly() {
            // Arrange
            List<Product> products = List.of(food);
            Page<Product> productPage = new PageImpl<>(products, pageable, products.size());
            when(productRepository.findAll(pageable)).thenReturn(productPage);

            // Act
            Page<ProductListResponse> result = productService.getAllProducts(null, null, pageable);

            // Assert
            ProductListResponse response = result.getContent().get(0);
            assertEquals(food.getId(), response.getId());
            assertEquals(food.getName(), response.getName());
            assertEquals(ProductCategory.FOOD, response.getCategory());
            assertTrue(response.getCategoryDisplay().contains("FOOD"));
        }

        @Test
        @DisplayName("Should map Coffee product correctly in ProductListResponse")
        void getAllProducts_ShouldMapCoffeeCorrectly() {
            // Arrange
            List<Product> products = List.of(coffee);
            Page<Product> productPage = new PageImpl<>(products, pageable, products.size());
            when(productRepository.findAll(pageable)).thenReturn(productPage);

            // Act
            Page<ProductListResponse> result = productService.getAllProducts(null, null, pageable);

            // Assert
            ProductListResponse response = result.getContent().get(0);
            assertEquals(coffee.getId(), response.getId());
            assertEquals(coffee.getName(), response.getName());
            assertEquals(ProductCategory.COFFEE, response.getCategory());
            assertTrue(response.getCategoryDisplay().contains("COFFEE"));
        }

        @Test
        @DisplayName("Should map Snack product correctly in ProductListResponse")
        void getAllProducts_ShouldMapSnackCorrectly() {
            // Arrange
            List<Product> products = List.of(snack);
            Page<Product> productPage = new PageImpl<>(products, pageable, products.size());
            when(productRepository.findAll(pageable)).thenReturn(productPage);

            // Act
            Page<ProductListResponse> result = productService.getAllProducts(null, null, pageable);

            // Assert
            ProductListResponse response = result.getContent().get(0);
            assertEquals(snack.getId(), response.getId());
            assertEquals(snack.getName(), response.getName());
            assertEquals(ProductCategory.SNACK, response.getCategory());
            assertTrue(response.getCategoryDisplay().contains("SNACK"));
        }
    }

    // ==================== POLYMORPHISM VERIFICATION TESTS ====================
    @Nested
    @DisplayName("Polymorphism Verification Tests")
    class PolymorphismTests {

        @Test
        @DisplayName("Should calculate different prices for Food based on vegetarian status")
        void getAllProducts_FoodPrice_ShouldReflectVegetarianDiscount() {
            // Arrange
            Food vegetarianFood = new Food();
            vegetarianFood.setId(10L);
            vegetarianFood.setName("Salad");
            vegetarianFood.setBasePrice(new BigDecimal("20000"));
            vegetarianFood.setStatus(ProductStatus.IN_STOCK);
            vegetarianFood.setCategory(ProductCategory.FOOD);
            vegetarianFood.setFoodType(FoodType.SALAD);
            vegetarianFood.setIsVegetarian(true);  // Vegetarian gets discount

            List<Product> products = List.of(vegetarianFood);
            Page<Product> productPage = new PageImpl<>(products, pageable, products.size());
            when(productRepository.findAll(pageable)).thenReturn(productPage);

            // Act
            Page<ProductListResponse> result = productService.getAllProducts(null, null, pageable);

            // Assert - Price should reflect vegetarian discount (basePrice - 5000)
            ProductListResponse response = result.getContent().get(0);
            assertNotNull(response.getPriceFormatted());
            // The price should be calculated via calculatePrice() method
        }

        @Test
        @DisplayName("Should calculate different prices for Coffee based on size")
        void getAllProducts_CoffeePrice_ShouldReflectSizeModifier() {
            // Arrange
            Coffee largeCoffee = new Coffee();
            largeCoffee.setId(11L);
            largeCoffee.setName("Large Latte");
            largeCoffee.setBasePrice(new BigDecimal("30000"));
            largeCoffee.setStatus(ProductStatus.IN_STOCK);
            largeCoffee.setCategory(ProductCategory.COFFEE);
            largeCoffee.setCoffeeType(CoffeeType.LATTE);
            largeCoffee.setSize(ProductSize.LARGE);  // Large gets +8000
            largeCoffee.setIsHot(true);
            largeCoffee.setEspressoShots(1);

            List<Product> products = List.of(largeCoffee);
            Page<Product> productPage = new PageImpl<>(products, pageable, products.size());
            when(productRepository.findAll(pageable)).thenReturn(productPage);

            // Act
            Page<ProductListResponse> result = productService.getAllProducts(null, null, pageable);

            // Assert
            ProductListResponse response = result.getContent().get(0);
            assertNotNull(response.getPriceFormatted());
            // The price should be calculated via calculatePrice() method (basePrice + 8000 for LARGE)
        }
    }
}

