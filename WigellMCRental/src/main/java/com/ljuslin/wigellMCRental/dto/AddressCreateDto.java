package com.ljuslin.wigellMCRental.dto;

import jakarta.validation.constraints.NotBlank;

public record AddressCreateDto(
        @NotBlank(message = "Gatuadress krävs") String street,
        @NotBlank(message = "Postkod krävs") String zipCode,
        @NotBlank(message = "Stad krävs") String city) {
}
