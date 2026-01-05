package com.foodbev.FoodBevApp.service.product;

import com.foodbev.FoodBevApp.dto.product.ProductRankingDTO;
import java.util.List;

public interface ProductRankingService {
    List<ProductRankingDTO> getTopSellingProducts();
}
