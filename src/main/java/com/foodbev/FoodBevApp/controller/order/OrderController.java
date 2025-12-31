package com.foodbev.FoodBevApp.controller.order;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.foodbev.FoodBevApp.entity.cart.CartItem;
import com.foodbev.FoodBevApp.entity.order.Order;
import com.foodbev.FoodBevApp.entity.user.User;
import com.foodbev.FoodBevApp.service.cart.CartService;
import com.foodbev.FoodBevApp.service.order.OrderService;
import com.foodbev.FoodBevApp.service.user.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/user/order")
@RequiredArgsConstructor
public class OrderController {

    private final CartService cartService;
    private final UserService userService;
    private final OrderService orderService;

    // ================= ORDER DETAIL PAGE =================
    @GetMapping("/detail")
    public String showOrderDetail(Model model, Principal principal) {
        
        if (principal == null) {
            return "redirect:/login";
        }

        User user = userService.findByEmail(principal.getName());
        if (user == null) {
            return "redirect:/login";
        }

        // Get cart items
        List<CartItem> cartItems = cartService.getUserCart(user);
        
        if (cartItems.isEmpty()) {
            return "redirect:/user/cart";
        }

        // Calculate totals
        BigDecimal subtotal = cartItems.stream()
            .map(item -> item.getProduct()
                .calculatePrice()
                .multiply(BigDecimal.valueOf(item.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal tax = subtotal.multiply(BigDecimal.valueOf(0.10));
        BigDecimal grandTotal = subtotal.add(tax);

        // Add to model
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("subtotal", subtotal);
        model.addAttribute("tax", tax);
        model.addAttribute("grandTotal", grandTotal);
        model.addAttribute("user", user);

        return "order/order";
    }

    // ================= PLACE ORDER =================
    @PostMapping("/place")
    public String placeOrder(
            @RequestParam String orderType,
            @RequestParam String customerName,
            @RequestParam String customerPhone,
            @RequestParam(required = false) String customerEmail,
            @RequestParam(required = false) Integer tableNumber,
            @RequestParam(required = false) Integer numberOfPeople,
            @RequestParam(required = false) String pickupTime,
            @RequestParam(required = false) String orderNotes,
            Principal principal) {

        User user = userService.findByEmail(principal.getName());
        
        // Create order
        Order order = orderService.createOfflineOrder(
            user,
            orderType,
            customerName,
            customerPhone,
            customerEmail,
            tableNumber,
            numberOfPeople,
            pickupTime,
            orderNotes
        );

        return "redirect:/user/order/confirmation/" + order.getId();
    }

    // ================= ORDER CONFIRMATION PAGE =================
    @GetMapping("/confirmation/{orderId}")
    public String orderConfirmation(
            @PathVariable Long orderId, 
            Model model, 
            Principal principal) {

        User user = userService.findByEmail(principal.getName());
        Order order = orderService.getOrderById(orderId, user);

        if (order == null) {
            return "redirect:/user/cart";
        }

        model.addAttribute("order", order);
        
        return "order/order-confirmation";
    }

    // ================= PAYMENT PAGE =================
    @GetMapping("/payment/{orderId}")
    public String paymentPage(
            @PathVariable Long orderId, 
            Model model, 
            Principal principal) {

        User user = userService.findByEmail(principal.getName());
        Order order = orderService.getOrderById(orderId, user);

        if (order == null) {
            return "redirect:/user/cart";
        }

        model.addAttribute("order", order);
        
        return "order/payment";
    }

    // ================= ORDER HISTORY =================
    @GetMapping("/history")
    public String orderHistory(Model model, Principal principal) {
        
        if (principal == null) {
            return "redirect:/login";
        }

        User user = userService.findByEmail(principal.getName());
        List<Order> orders = orderService.getUserOrders(user);

        model.addAttribute("orders", orders);
        
        return "order/order-history";
    }
}
