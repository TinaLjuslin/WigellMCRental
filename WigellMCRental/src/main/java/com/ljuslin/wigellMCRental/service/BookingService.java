package com.ljuslin.wigellMCRental.service;

import com.ljuslin.wigellMCRental.dto.*;

import java.time.LocalDate;
import java.util.List;

public interface BookingService {
    BookingResponseDto createBooking(BookingCreateDto dto);

    BookingResponseDto userPatchBooking(Long bookingId, BookingPatchUserDto dto);

    BookingResponseDto adminPatchBooking(Long bookingId, BookingPatchAdminDto dto);


    BookingResponseDto getBooking(Long bookingId);

    BookingResponseDto updateBooking(Long bookingId, BookingUpdateDto dto);

    void deleteBooking(Long bookingId);


}
