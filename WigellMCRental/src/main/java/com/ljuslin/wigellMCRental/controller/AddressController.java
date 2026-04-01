package com.ljuslin.wigellMCRental.controller;

import com.ljuslin.wigellMCRental.dto.AddressCreateDto;
import com.ljuslin.wigellMCRental.dto.AddressResponseDto;
import com.ljuslin.wigellMCRental.service.AddressServiceImpl;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/customers/{customerId}/addresses")
public class AddressController {
    private static final Logger logger = LoggerFactory.getLogger(AddressController.class);
    private final AddressServiceImpl addressService;

    public AddressController(AddressServiceImpl addressService) {
        this.addressService = addressService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<AddressResponseDto> addAddress(
            @PathVariable Long customerId,
            @Valid @RequestBody AddressCreateDto dto) {
        logger.debug("Adding new address to customer with id: {}", customerId);
        AddressResponseDto responseDto = addressService.addAddress(customerId, dto);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(responseDto.id())
                .toUri();
        return ResponseEntity.created(location).body(responseDto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{addressId}")
    public ResponseEntity<Void> deleteAddress(@PathVariable Long customerId,
                                           @PathVariable Long addressId) {
        logger.debug("Admin is deleting address {} for customer {}", addressId, customerId);
        addressService.removeAddress(customerId, addressId);
        return ResponseEntity.noContent().build();
    }
}
