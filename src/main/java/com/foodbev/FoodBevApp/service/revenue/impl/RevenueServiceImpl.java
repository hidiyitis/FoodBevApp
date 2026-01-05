package com.foodbev.FoodBevApp.service.revenue.impl;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.foodbev.FoodBevApp.dto.revenue.DailyRevenueDTO;
import com.foodbev.FoodBevApp.repository.order.OrderRepository;
import com.foodbev.FoodBevApp.service.revenue.RevenueService;

@Service
public class RevenueServiceImpl implements RevenueService {

    @Autowired
    private OrderRepository orderRepository;

    @Override
    public List<DailyRevenueDTO> getDailyRevenue() {
        List<Object[]> results = orderRepository.getDailyRevenue();
        List<DailyRevenueDTO> dailyRevenueDTOs = new ArrayList<>();

        for (Object[] row : results) {
            LocalDate date = ((Date) row[0]).toLocalDate();
            Integer totalProducts = row[1] != null ? ((Number) row[1]).intValue() : 0;
            BigDecimal totalRevenue = row[2] != null ? new BigDecimal(row[2].toString()) : BigDecimal.ZERO;

            dailyRevenueDTOs.add(new DailyRevenueDTO(date, totalProducts, totalRevenue));
        }

        return dailyRevenueDTOs;
    }

    @Override
    public BigDecimal getTotalRevenue() {
        BigDecimal totalRevenue = orderRepository.getTotalRevenue();
        return totalRevenue != null ? totalRevenue : BigDecimal.ZERO;
    }

    @Override
    public BigDecimal getTodayRevenue() {
        BigDecimal todayRevenue = orderRepository.getTodayRevenue();
        return todayRevenue != null ? todayRevenue : BigDecimal.ZERO;
    }
}
