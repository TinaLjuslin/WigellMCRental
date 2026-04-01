package com.ljuslin.wigellMCRental.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record BookingUpdateDto(
        @NotNull(message = "Customer ID krävs")
        Long customerId,

        @NotNull(message = "Bike ID krävs")
        Long bikeId,

        @NotNull(message = "Startdatum krävs")
        @FutureOrPresent(message = "Startdatum kan inte vara i det förflutna")
        LocalDate startDate,

        @NotNull(message = "Slutdatum krävs")
        LocalDate endDate
) {}