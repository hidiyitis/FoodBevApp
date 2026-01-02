package com.foodbev.FoodBevApp.dto.payment;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MidtransSnapResponse {

    @JsonProperty("token")
    private String token;

    @JsonProperty("redirect_url")
    private String redirectUrl;

    @JsonProperty("error_messages")
    private String[] errorMessages;
}
