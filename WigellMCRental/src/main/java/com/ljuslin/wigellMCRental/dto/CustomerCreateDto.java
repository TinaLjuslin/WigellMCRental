package com.ljuslin.wigellMCRental.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
public record CustomerCreateDto(
        @NotBlank(message = "Förnamn krävs")
        String firstName,

        @NotBlank(message = "Efternamn krävs")
        String lastName,

        @NotBlank(message = "E-post krävs")
        @Email(message = "Ogiltigt e-postformat")
        String email,

        @NotBlank(message = "Användarnamn krävs")
        @Size(min = 4, message = "Användarnamnet måste vara minst 4 tecken")
        String username,

        @NotBlank(message = "Lösenord krävs")
        @Size(min = 4, message = "Lösenordet måste vara minst 4 tecken")
        String password,

        @NotNull(message = "Minst en adress krävs")
        List<AddressCreateDto> address

) {
}