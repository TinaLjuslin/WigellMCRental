package com.ljuslin.wigellMCRental.mapper;

import com.ljuslin.wigellMCRental.dto.BookingCreateDto;
import com.ljuslin.wigellMCRental.dto.BookingResponseDto;
import com.ljuslin.wigellMCRental.entity.Bike;
import com.ljuslin.wigellMCRental.entity.Booking;
import com.ljuslin.wigellMCRental.entity.Customer;
import com.ljuslin.wigellMCRental.mapper.BikeMapper;

import java.math.BigDecimal;
import java.time.LocalDate;

public final class BookingMapper {

    public static BookingResponseDto toDto(Booking booking ) {
        return new BookingResponseDto(
                booking.getId(),
                booking.getCustomer().getId(),
                booking.getCustomer().getFirstName() + " " + booking.getCustomer().getLastName(),
                BikeMapper.toDto(booking.getBike()), // Vi återanvänder din BikeMapper!
                booking.getStartDate(),
                booking.getEndDate(),
                booking.getBookingDate(),
                booking.getTotalPriceSek(),
                booking.getTotalPriceGbp(),
                booking.isPaid()

        );
    }

    // toEntityCreate används i din Service
    public static Booking toEntityCreate(BookingCreateDto dto, Customer customer, Bike bike,
                                         BigDecimal totalPriceSek, BigDecimal totalPriceGbp) {
        return new Booking(
                null,
                customer,
                bike,
                dto.startDate(),
                dto.endDate(),
                LocalDate.now(),
                totalPriceSek,
                totalPriceGbp,
                false
        );
    }
}