package com.foodbev.FoodBevApp.dto.payment;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {

    private Long id;
    private Long orderId;
    private String transactionId;
    private String midtransOrderId;
    private String snapToken;
    private String redirectUrl;
    private String paymentType;
    private BigDecimal grossAmount;
    private String status;
    private String vaNumber;
    private String bank;
    private String paymentCode;
    private String billKey;
    private String billerCode;
    private String qrCodeUrl;
    private LocalDateTime expiryTime;
    private LocalDateTime settlementTime;
    private LocalDateTime createdAt;
}
