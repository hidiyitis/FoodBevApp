package com.foodbev.FoodBevApp.service.cart;

import com.foodbev.FoodBevApp.entity.cart.CartItem;
import com.foodbev.FoodBevApp.entity.user.User;

import java.util.List;

public interface CartService {

    void addToCart(User user, Long productId, int quantity);

    List<CartItem> getUserCart(User user);

    void updateQuantity(User user, Long productId, int quantity);

    void removeItem(User user, Long productId);

    void clearCart(User user);
}

