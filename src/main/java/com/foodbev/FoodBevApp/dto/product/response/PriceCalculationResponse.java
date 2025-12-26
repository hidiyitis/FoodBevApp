package com.foodbev.FoodBevApp.dto.product.response;

import lombok.*;

import java.math.BigDecimal;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PriceCalculationResponse {

    private Long productId;
    private String productName;
    private BigDecimal basePrice;
    private BigDecimal calculatedPrice;
    private Map<String, BigDecimal> priceBreakdown; // untuk menampilkan detail perhitungan
    private String formattedPrice; // "Rp 30.000"
}

