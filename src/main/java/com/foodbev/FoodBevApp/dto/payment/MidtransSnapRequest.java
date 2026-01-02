package com.foodbev.FoodBevApp.dto.payment;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MidtransSnapRequest {

    @JsonProperty("transaction_details")
    private TransactionDetails transactionDetails;

    @JsonProperty("customer_details")
    private CustomerDetails customerDetails;

    @JsonProperty("item_details")
    private List<ItemDetail> itemDetails;

    @JsonProperty("callbacks")
    private Callbacks callbacks;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TransactionDetails {
        @JsonProperty("order_id")
        private String orderId;

        @JsonProperty("gross_amount")
        private Long grossAmount;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustomerDetails {
        @JsonProperty("first_name")
        private String firstName;

        @JsonProperty("last_name")
        private String lastName;

        @JsonProperty("email")
        private String email;

        @JsonProperty("phone")
        private String phone;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemDetail {
        @JsonProperty("id")
        private String id;

        @JsonProperty("price")
        private Long price;

        @JsonProperty("quantity")
        private Integer quantity;

        @JsonProperty("name")
        private String name;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Callbacks {
        @JsonProperty("finish")
        private String finish;

        @JsonProperty("error")
        private String error;

        @JsonProperty("pending")
        private String pending;
    }
}
