package com.ljuslin.wigellMCRental.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CustomerCreateDto(
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotBlank @Email String email,
        @NotBlank @Size(min = 4) String username,
        @NotBlank @Size(min = 4) String password,
        @NotNull List<AddressCreateDto> address,
        String keycloakId
) {
}
