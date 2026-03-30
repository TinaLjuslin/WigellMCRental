package com.ljuslin.wigellMCRental.controller;

import com.ljuslin.wigellMCRental.dto.BikeCreateDto;
import com.ljuslin.wigellMCRental.dto.BikeResponseDto;
import com.ljuslin.wigellMCRental.dto.BookingCreateDto;
import com.ljuslin.wigellMCRental.dto.BookingResponseDto;
import com.ljuslin.wigellMCRental.service.BookingServiceImpl;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/bookings")
public class BookingController {
    private static final Logger logger = LoggerFactory.getLogger(BikeController.class);
    private final BookingServiceImpl bookingService;

    public BookingController(BookingServiceImpl bookingService) {
        this.bookingService = bookingService;
    }@PreAuthorize("hasRole('USER')")
    @PostMapping
    public ResponseEntity<BookingResponseDto> createBike(@RequestBody @Valid BookingCreateDto dto) {
        logger.info("User saving new booking");
        try {
            BookingResponseDto bookingResponseDto = bookingService.createBooking(dto);
            logger.debug("Successfully created booking");
            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(bookingResponseDto.id())
                    .toUri();

            return ResponseEntity.created(location).body(bookingResponseDto);
        } catch (Exception e) {
            logger.error("Failed to create booking", e.getMessage());
            throw e;
        }
    }

    }
