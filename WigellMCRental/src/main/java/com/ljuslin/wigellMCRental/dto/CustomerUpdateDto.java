package com.ljuslin.wigellMCRental.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CustomerUpdateDto(
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotBlank @Email String email
) {
}
