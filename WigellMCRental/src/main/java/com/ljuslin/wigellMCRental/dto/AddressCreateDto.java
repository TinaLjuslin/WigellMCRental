package com.ljuslin.wigellMCRental.dto;

import jakarta.validation.constraints.NotBlank;

public record AddressCreateDto(
        @NotBlank String street,
        @NotBlank String zipCode,
        @NotBlank String city) {
    }
