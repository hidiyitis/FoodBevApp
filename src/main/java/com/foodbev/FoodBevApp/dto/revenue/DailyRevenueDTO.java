package com.foodbev.FoodBevApp.dto.revenue;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyRevenueDTO {
    private LocalDate date;
    private Integer totalProductsSold;
    private BigDecimal totalRevenue;
}
