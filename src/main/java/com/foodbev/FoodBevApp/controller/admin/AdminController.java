package com.foodbev.FoodBevApp.controller.admin;

import com.foodbev.FoodBevApp.service.product.ProductRankingService;
import com.foodbev.FoodBevApp.service.revenue.RevenueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private ProductRankingService productRankingService;

    @Autowired
    private RevenueService revenueService;

    @GetMapping("/dashboard")
    public String adminDashboard(Model model) {
        Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("username", auth.getName());
        model.addAttribute("isAdmin", true);
        model.addAttribute("topSellingProducts", productRankingService.getTopSellingProducts());
        model.addAttribute("totalRevenue", revenueService.getTotalRevenue());
        model.addAttribute("todayRevenue", revenueService.getTodayRevenue());
        return "admin/dashboard";
    }

    @GetMapping("/revenue")
    public String revenueReport(Model model) {
        Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("username", auth.getName());
        model.addAttribute("isAdmin", true);
        model.addAttribute("dailyRevenue", revenueService.getDailyRevenue());
        return "admin/revenue";
    }
}