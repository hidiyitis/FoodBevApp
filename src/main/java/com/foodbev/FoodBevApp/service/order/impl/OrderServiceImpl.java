package com.foodbev.FoodBevApp.service.order.impl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.foodbev.FoodBevApp.entity.cart.CartItem;
import com.foodbev.FoodBevApp.entity.order.Order;
import com.foodbev.FoodBevApp.entity.order.OrderItem;
import com.foodbev.FoodBevApp.entity.user.User;
import com.foodbev.FoodBevApp.repository.order.OrderRepository;
import com.foodbev.FoodBevApp.service.cart.CartService;
import com.foodbev.FoodBevApp.service.order.OrderService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CartService cartService;

    @Override
    @Transactional
    public Order createOfflineOrder(User user, String orderType, String customerName,
                                   String customerPhone, String customerEmail,
                                   Integer tableNumber, Integer numberOfPeople,
                                   String pickupTime, String orderNotes) {

        log.info("Creating offline order for user: {} with type: {}", user.getEmail(), orderType);

        // Get cart items
        List<CartItem> cartItems = cartService.getUserCart(user);
        
        if (cartItems.isEmpty()) {
            log.error("Cart is empty for user: {}", user.getEmail());
            throw new RuntimeException("Cart is empty");
        }

        log.info("Found {} items in cart", cartItems.size());

        // Calculate totals
        BigDecimal subtotal = cartItems.stream()
            .map(item -> item.getProduct().calculatePrice()
                .multiply(BigDecimal.valueOf(item.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal tax = subtotal.multiply(BigDecimal.valueOf(0.10));
        BigDecimal totalAmount = subtotal.add(tax);

        // Create order
        Order order = new Order();
        order.setUser(user);
        order.setOrderType(Order.OrderType.valueOf(orderType));
        order.setCustomerName(customerName);
        order.setCustomerPhone(customerPhone);
        order.setCustomerEmail(customerEmail);
        order.setTableNumber(tableNumber);
        order.setNumberOfPeople(numberOfPeople);
        order.setPickupTime(pickupTime);
        order.setOrderNotes(orderNotes);
        order.setSubtotal(subtotal);
        order.setTax(tax);
        order.setTotalAmount(totalAmount);
        order.setStatus(Order.OrderStatus.PENDING);

        // Create order items
        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setProductName(cartItem.getProduct().getName());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPriceAtOrder(cartItem.getProduct().calculatePrice());
            orderItem.setSubtotal(cartItem.getProduct().calculatePrice()
                .multiply(BigDecimal.valueOf(cartItem.getQuantity())));
            
            order.getOrderItems().add(orderItem);
        }

        // Save order
        log.info("Saving order with {} items", order.getOrderItems().size());
        Order savedOrder = orderRepository.save(order);
        log.info("Order saved successfully with ID: {}", savedOrder.getId());
        
        // Clear cart after successful order
        cartService.clearCart(user);
        log.info("Cart cleared for user: {}", user.getEmail());

        return savedOrder;
    }

    @Override
    public Order getOrderById(Long orderId, User user) {
        return orderRepository.findById(orderId)
            .filter(order -> order.getUser().getId().equals(user.getId()))
            .orElse(null);
    }

    @Override
    public List<Order> getUserOrders(User user) {
        return orderRepository.findByUserOrderByCreatedAtDesc(user);
    }
}
