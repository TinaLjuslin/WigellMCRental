package com.ljuslin.wigellMCRental.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record BookingResponseDto(
        Long id,
        Long customerId,    // Bra för referens
        String customerName, // Snyggt att visa direkt: t.ex. "Erik ljuslin"
        BikeResponseDto bike, // Hela MC-infon (märke, modell etc.)
        LocalDate startDate,
        LocalDate endDate,
        LocalDate bookingDate,
        BigDecimal totalPriceSEK,
        BigDecimal totalPriceGBP,
        boolean paid
) {}