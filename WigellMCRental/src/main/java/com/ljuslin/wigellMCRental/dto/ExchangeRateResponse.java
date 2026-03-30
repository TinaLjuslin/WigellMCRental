package com.ljuslin.wigellMCRental.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record ExchangeRateResponse(
        String result,
        @JsonProperty("conversion_rate") BigDecimal conversionRate
) {
}
