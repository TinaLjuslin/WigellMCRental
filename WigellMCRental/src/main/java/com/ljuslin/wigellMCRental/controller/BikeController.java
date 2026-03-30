package com.ljuslin.wigellMCRental.controller;

import com.ljuslin.wigellMCRental.dto.BikeResponseDto;
import com.ljuslin.wigellMCRental.dto.BikeCreateDto;
import com.ljuslin.wigellMCRental.service.BikeServiceImpl;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/bikes")
public class BikeController {
    private static final Logger logger = LoggerFactory.getLogger(BikeController.class);
    private final BikeServiceImpl bikeService;

    public BikeController(BikeServiceImpl bikeService) {
        this.bikeService = bikeService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<BikeResponseDto>> getAllBikes() {
        logger.info("Admin requested all bikes from the database");

        try {
            List<BikeResponseDto> bikes = bikeService.getAllBikes();
            logger.debug("Successfully retrieved {} bikes", bikes.size());
            return ResponseEntity.ok(bikes);
        } catch (Exception e) {
            logger.error("Failed to retrieve bikes: {}", e.getMessage());
            throw e;
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<BikeResponseDto> createBike(@RequestBody @Valid BikeCreateDto dto) {
        logger.info("Admin saving new bike: {} {}", dto.brand(), dto.model());
        try {
            BikeResponseDto bikeResponseDto = bikeService.createBike(dto);
            logger.debug("Successfully created bike");
            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(bikeResponseDto.id())
                    .toUri();

            return ResponseEntity.created(location).body(bikeResponseDto);
        } catch (Exception e) {
            logger.error("Failed to create bike", e.getMessage());
            throw e;
        }
    }
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/availability")
    public ResponseEntity<List<BikeResponseDto>> getAvailableBikes(
            @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

        logger.info("Requesting available bikes from {} to {}", from, to);
        List<BikeResponseDto> bikes = bikeService.getAvailableBikes(from, to);
        return ResponseEntity.ok(bikes);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
public ResponseEntity<Void> deleteBike(@PathVariable long id) {
        logger.info("Trying to delete bike with id {}", id);
        bikeService.delete(id);
        return ResponseEntity.noContent().build();

    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<BikeResponseDto> getBike(@PathVariable long id) {
        try {
            BikeResponseDto bike = bikeService.getBike(id);
            logger.debug("Successfully retrieved bike with id ", id);
            return ResponseEntity.ok(bike);
        } catch (Exception e) {
            logger.error("Failed to retrieve bike ", e.getMessage());
            throw e;
        }
    }


}
