package com.foodbev.FoodBevApp.service.product.impl;

import com.foodbev.FoodBevApp.dto.product.ProductRankingDTO;
import com.foodbev.FoodBevApp.repository.order.OrderItemRepository;
import com.foodbev.FoodBevApp.service.product.ProductRankingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductRankingServiceImpl implements ProductRankingService {

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Override
    public List<ProductRankingDTO> getTopSellingProducts() {
        List<Object[]> results = orderItemRepository.getTopSellingProducts();
        List<ProductRankingDTO> rankings = new ArrayList<>();
        
        int rank = 1;
        for (Object[] row : results) {
            ProductRankingDTO dto = new ProductRankingDTO();
            dto.setRank(rank);
            dto.setProductName((String) row[0]);
            dto.setUnitsSold(((Number) row[1]).longValue());
            rankings.add(dto);
            rank++;
        }
        
        return rankings;
    }
}
