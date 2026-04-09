package com.ljuslin.wigellMCRental.controller;

import com.ljuslin.wigellMCRental.dto.*;
import com.ljuslin.wigellMCRental.service.BookingServiceImpl;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/bookings")
public class BookingController {
    private static final Logger logger = LoggerFactory.getLogger(BookingController.class);
    private final BookingServiceImpl bookingService;

    public BookingController(BookingServiceImpl bookingService) {
        this.bookingService = bookingService;
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public ResponseEntity<BookingResponseDto> createBooking(@RequestBody @Valid BookingCreateDto dto) {
        logger.info("User saving new booking");
        BookingResponseDto bookingResponseDto = bookingService.createBooking(dto);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(bookingResponseDto.id())
                .toUri();

        return ResponseEntity.created(location).body(bookingResponseDto);

    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping
    public ResponseEntity<List<BookingResponseDto>> getBookings(
            @RequestParam(value = "customerId", required = false) Long customerId) {
        logger.info("Request to list bookings (customerId filter: {})", customerId);
        List<BookingResponseDto> bookings = bookingService.getAllOrFiltered(customerId);
        return ResponseEntity.ok(bookings);
        }

        @PreAuthorize("hasRole('USER')")
        @PatchMapping("{id}")
        public ResponseEntity<BookingResponseDto> userPatchBooking (@PathVariable Long id,
                @RequestBody @Valid BookingPatchUserDto dto){
            logger.info("User trying to patch booking with id: {}", id);
            return ResponseEntity.ok(bookingService.userPatchBooking(id, dto));
        }

        @PreAuthorize("hasRole('ADMIN')")
        @PatchMapping("{id}/status")
        public ResponseEntity<BookingResponseDto> adminPatchBooking (@PathVariable Long id,
                @RequestBody @Valid BookingPatchAdminDto dto){
            logger.info("Admin trying to patch booking with id: {}", id);
            return ResponseEntity.ok(bookingService.adminPatchBooking(id, dto));
        }

        @PreAuthorize("hasRole('ADMIN')")
        @GetMapping("{id}")
        public ResponseEntity<BookingResponseDto> getBooking (@PathVariable Long id){
            logger.info("Admin trying to get booking with id: {}", id);
            BookingResponseDto dto = bookingService.getBooking(id);
            return ResponseEntity.ok(dto);
        }

        @PreAuthorize("hasRole('ADMIN')")
        @PutMapping("{id}")
        public ResponseEntity<BookingResponseDto> updateBooking (@PathVariable Long id,
                @RequestBody @Valid BookingUpdateDto dto){
            logger.info("Admin trying to update booking with id {}", id);
            return ResponseEntity.ok(bookingService.updateBooking(id, dto));
        }

        @PreAuthorize("hasRole('ADMIN')")
        @DeleteMapping("{id}")
        public ResponseEntity<Void> deleteBooking (@PathVariable Long id){
            logger.info("Trying to delete booking with id {}", id);
            bookingService.deleteBooking(id);
            return ResponseEntity.noContent().build();
        }
    }
