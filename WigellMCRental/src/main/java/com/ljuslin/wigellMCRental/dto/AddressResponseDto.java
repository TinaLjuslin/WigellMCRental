package com.ljuslin.wigellMCRental.dto;

import jakarta.validation.constraints.NotBlank;

public record AddressResponseDto(
        Long id,
        String street,
        String zipCode,
        String city) {

}
