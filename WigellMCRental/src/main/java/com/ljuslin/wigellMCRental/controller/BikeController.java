package com.ljuslin.wigellMCRental.controller;

import com.ljuslin.wigellMCRental.dto.BikeResponseDto;
import com.ljuslin.wigellMCRental.dto.BikeCreateDto;
import com.ljuslin.wigellMCRental.dto.BikeUpdateDto;
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
        logger.debug("Admin requested all bikes from the database");

        List<BikeResponseDto> bikes = bikeService.getAllBikes();
        return ResponseEntity.ok(bikes);

    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<BikeResponseDto> createBike(@RequestBody @Valid BikeCreateDto dto) {
        logger.debug("Admin saving new bike: {} {}", dto.brand(), dto.model());
        BikeResponseDto bikeResponseDto = bikeService.createBike(dto);
        logger.debug("Successfully created bike");
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(bikeResponseDto.id())
                .toUri();

        return ResponseEntity.created(location).body(bikeResponseDto);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/availability")
    public ResponseEntity<List<BikeResponseDto>> getAvailableBikes(
            @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

        logger.debug("Requesting available bikes from {} to {}", from, to);
        List<BikeResponseDto> bikes = bikeService.getAvailableBikes(from, to);
        return ResponseEntity.ok(bikes);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBike(@PathVariable long id) {
        logger.debug("Trying to delete bike with id {}", id);
        bikeService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<BikeResponseDto> getBike(@PathVariable long id) {
        BikeResponseDto bikeDto = bikeService.getBike(id);
        logger.debug("Successfully retrieved bike with id ", id);
        return ResponseEntity.ok(bikeDto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<BikeResponseDto> updateBike(@PathVariable long id,
                                                      @RequestBody @Valid BikeUpdateDto dto) {
        logger.debug("Admin trying to update bike with id {}", id);
        return ResponseEntity.ok(bikeService.updateBike(id, dto));
    }
}