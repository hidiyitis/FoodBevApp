package com.foodbev.FoodBevApp.repository.product;

import com.foodbev.FoodBevApp.entity.product.Product;
import com.foodbev.FoodBevApp.entity.product.enums.ProductCategory;
import com.foodbev.FoodBevApp.entity.product.enums.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // Paginated queries
    Page<Product> findByCategory(ProductCategory category, Pageable pageable);

    Page<Product> findByStatus(ProductStatus status, Pageable pageable);

    Page<Product> findByCategoryAndStatus(ProductCategory category, ProductStatus status, Pageable pageable);

    // Find by category
    List<Product> findByCategory(ProductCategory category);

    List<Product> findByStatus(ProductStatus status);

    List<Product> findByCategoryAndStatus(ProductCategory category, ProductStatus status);

    List<Product> findByNameContainingIgnoreCase(String name);

    // Search methods with pagination
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Product> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE (LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND p.status = :status")
    Page<Product> searchByKeywordAndStatus(@Param("keyword") String keyword,
                                            @Param("status") ProductStatus status,
                                            Pageable pageable);

    @Query("SELECT p FROM Product p WHERE (LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND p.category = :category")
    Page<Product> searchByKeywordAndCategory(@Param("keyword") String keyword,
                                              @Param("category") ProductCategory category,
                                              Pageable pageable);

    @Query("SELECT p FROM Product p WHERE (LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "AND p.category = :category AND p.status = :status")
    Page<Product> searchByKeywordAndCategoryAndStatus(@Param("keyword") String keyword,
                                                       @Param("category") ProductCategory category,
                                                       @Param("status") ProductStatus status,
                                                       Pageable pageable);

    @Modifying
    @Query("UPDATE Product p SET p.status = :status, p.updatedAt = CURRENT_TIMESTAMP WHERE p.id = :id")
    int updateStatus(@Param("id") Long id, @Param("status") ProductStatus status);

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Product p WHERE p.id = :id AND p.status = 'IN_STOCK'")
    boolean existsByIdAndInStock(@Param("id") Long id);

    List<Product> findAllByOrderByNameAsc();

    @Query("SELECT p FROM Product p WHERE p.basePrice BETWEEN :minPrice AND :maxPrice")
    List<Product> findByPriceRange(@Param("minPrice") java.math.BigDecimal minPrice,
                                   @Param("maxPrice") java.math.BigDecimal maxPrice);
}
