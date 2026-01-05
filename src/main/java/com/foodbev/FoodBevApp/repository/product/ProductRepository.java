package com.foodbev.FoodBevApp.repository.product;

import com.foodbev.FoodBevApp.entity.product.Product;
import com.foodbev.FoodBevApp.entity.product.enums.ProductCategory;
import com.foodbev.FoodBevApp.entity.product.enums.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // Paginated queries
    Page<Product> findByCategory(ProductCategory category, Pageable pageable);

    Page<Product> findByStatus(ProductStatus status, Pageable pageable);

    Page<Product> findByCategoryAndStatus(ProductCategory category, ProductStatus status, Pageable pageable);

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
}
