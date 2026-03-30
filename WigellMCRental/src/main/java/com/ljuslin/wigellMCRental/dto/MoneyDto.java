package com.ljuslin.wigellMCRental.dto;

import java.math.BigDecimal;

public record MoneyDto(
        BigDecimal amount,
        String currency) {
}
