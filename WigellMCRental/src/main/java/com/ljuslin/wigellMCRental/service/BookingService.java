package com.ljuslin.wigellMCRental.service;

import com.ljuslin.wigellMCRental.dto.BookingCreateDto;
import com.ljuslin.wigellMCRental.dto.BookingResponseDto;

public interface BookingService {
    BookingResponseDto createBooking(BookingCreateDto dto);
}
