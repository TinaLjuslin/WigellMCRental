package com.ljuslin.wigellMCRental.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record BookingPatchUserDto(
        Long bikeId,

        LocalDate startDate,

        LocalDate endDate) {
}
