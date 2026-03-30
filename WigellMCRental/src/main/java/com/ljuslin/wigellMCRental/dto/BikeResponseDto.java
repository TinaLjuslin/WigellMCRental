package com.ljuslin.wigellMCRental.dto;

import com.ljuslin.wigellMCRental.entity.BikeStatus;

import java.math.BigDecimal;

public record BikeResponseDto(
        Long id,
        String brand,
        String engineSize,
        String model,
        int year,
        BigDecimal pricePerDay,
        String statusSwedish
) {
}

