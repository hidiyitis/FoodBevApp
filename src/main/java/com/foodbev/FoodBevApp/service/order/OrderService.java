package com.foodbev.FoodBevApp.service.order;

import java.util.List;

import com.foodbev.FoodBevApp.entity.order.Order;
import com.foodbev.FoodBevApp.entity.user.User;

public interface OrderService {
    Order createOfflineOrder(User user, String orderType, String customerName, 
                            String customerPhone, String customerEmail,
                            Integer tableNumber, Integer numberOfPeople, 
                            String pickupTime, String orderNotes);
    
    Order getOrderById(Long orderId, User user);
    
    List<Order> getUserOrders(User user);
}
