package com.ljuslin.wigellMCRental.controller;

import com.ljuslin.wigellMCRental.dto.AddressCreateDto;
import com.ljuslin.wigellMCRental.dto.AddressResponseDto;
import com.ljuslin.wigellMCRental.service.AddressServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/customers/{customerId}/addresses")
public class AddressController {
    private static final Logger logger = LoggerFactory.getLogger(AddressController.class);
    private final AddressServiceImpl addressService;

    public AddressController(AddressServiceImpl addressService) {
        this.addressService = addressService;
    }

    @PostMapping
    public ResponseEntity<AddressResponseDto> addAddress(
            @PathVariable Long customerId,
            @RequestBody AddressCreateDto dto) {
        return ResponseEntity.ok(addressService.addAddress(customerId, dto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{addressId}")
    public ResponseEntity<Void> deleteBike(@PathVariable Long customerId,
                                           @PathVariable Long addressId) {
        logger.info("Trying to delete address with id {}", addressId);
        addressService.removeAddress(customerId, addressId);
        return ResponseEntity.noContent().build();
    }
}
