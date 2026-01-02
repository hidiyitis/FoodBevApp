package com.foodbev.FoodBevApp.dto.payment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MidtransNotificationRequest {

    @JsonProperty("transaction_time")
    private String transactionTime;

    @JsonProperty("transaction_status")
    private String transactionStatus;

    @JsonProperty("transaction_id")
    private String transactionId;

    @JsonProperty("status_message")
    private String statusMessage;

    @JsonProperty("status_code")
    private String statusCode;

    @JsonProperty("signature_key")
    private String signatureKey;

    @JsonProperty("settlement_time")
    private String settlementTime;

    @JsonProperty("payment_type")
    private String paymentType;

    @JsonProperty("order_id")
    private String orderId;

    @JsonProperty("merchant_id")
    private String merchantId;

    @JsonProperty("gross_amount")
    private String grossAmount;

    @JsonProperty("fraud_status")
    private String fraudStatus;

    @JsonProperty("currency")
    private String currency;

    // Virtual Account fields
    @JsonProperty("va_numbers")
    private VaNumber[] vaNumbers;

    @JsonProperty("permata_va_number")
    private String permataVaNumber;

    // E-wallet fields
    @JsonProperty("acquirer")
    private String acquirer;

    // Mandiri Bill fields
    @JsonProperty("bill_key")
    private String billKey;

    @JsonProperty("biller_code")
    private String billerCode;

    // QRIS fields
    @JsonProperty("issuer")
    private String issuer;

    // Expiry
    @JsonProperty("expiry_time")
    private String expiryTime;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class VaNumber {
        @JsonProperty("bank")
        private String bank;

        @JsonProperty("va_number")
        private String vaNumber;
    }
}
