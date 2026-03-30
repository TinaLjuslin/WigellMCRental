package com.ljuslin.wigellMCRental.dto;

import java.math.BigDecimal;

public record BikeCreateDto(
        String brand,
        String engineSize,
        String model,
        int year,
        BigDecimal pricePerDay
) {}
