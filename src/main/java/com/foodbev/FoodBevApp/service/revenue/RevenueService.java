package com.foodbev.FoodBevApp.service.revenue;

import java.math.BigDecimal;
import java.util.List;

import com.foodbev.FoodBevApp.dto.revenue.DailyRevenueDTO;

public interface RevenueService {
    List<DailyRevenueDTO> getDailyRevenue();
    BigDecimal getTotalRevenue();
    BigDecimal getTodayRevenue();
}
