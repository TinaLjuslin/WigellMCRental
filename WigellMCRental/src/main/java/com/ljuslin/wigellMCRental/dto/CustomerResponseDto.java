package com.ljuslin.wigellMCRental.dto;

import java.util.List;

public record CustomerResponseDto(
        Long id,
        String firstName,
        String lastName,
        String email,
        String username,
        List<AddressResponseDto> addresses,
        String keycloakId
) {
}
