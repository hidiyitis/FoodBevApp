package com.foodbev.FoodBevApp.repository.cart;

import com.foodbev.FoodBevApp.entity.cart.CartItem;
import com.foodbev.FoodBevApp.entity.product.Product;
import com.foodbev.FoodBevApp.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    Optional<CartItem> findByUserAndProduct(User user, Product product);

    List<CartItem> findByUser(User user);

    void deleteByUser(User user);
}
