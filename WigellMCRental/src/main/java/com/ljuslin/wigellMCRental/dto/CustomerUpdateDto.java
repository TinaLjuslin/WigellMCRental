package com.ljuslin.wigellMCRental.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CustomerUpdateDto(
        @NotBlank(message = "Förnamn krävs")
        String firstName,

        @NotBlank(message = "Efternamn krävs")
        String lastName,

        @NotBlank(message = "E-post krävs")
        @Email(message = "Ogiltigt e-postformat")
        String email

        ) {
}
