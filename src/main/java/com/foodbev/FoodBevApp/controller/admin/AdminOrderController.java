package com.foodbev.FoodBevApp.controller.admin;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.foodbev.FoodBevApp.entity.order.Order;
import com.foodbev.FoodBevApp.repository.order.OrderRepository;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminOrderController {

    private final OrderRepository orderRepository;

    @GetMapping("/orders")
    public String orderView(Model model) {
        // Get all orders sorted by created date descending
        List<Order> orders = orderRepository.findAll();
        orders.sort((o1, o2) -> o2.getCreatedAt().compareTo(o1.getCreatedAt()));
        
        model.addAttribute("orders", orders);
        return "admin/order-view";
    }
}
