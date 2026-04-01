package com.ljuslin.wigellMCRental.dto;

import com.ljuslin.wigellMCRental.entity.BikeStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record BikeUpdateDto(
        @NotBlank(message = "Märke krävs") String brand,
        @NotBlank(message = "Motorkapacitet krävs") String engineSize,
        @NotBlank(message = "Modell krävs") String model,
        @Min(value = 1900, message = "Årsmodell måste vara minst 1900") int year,
        @NotNull(message = "Pris per dag krävs")@Min(value = 0, message = "Priset kan inte vara negativt")
        BigDecimal pricePerDay,
        @NotNull(message = "Status krävs") BikeStatus status
) {
}
