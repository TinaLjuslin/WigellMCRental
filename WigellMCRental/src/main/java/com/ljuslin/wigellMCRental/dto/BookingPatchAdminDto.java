package com.ljuslin.wigellMCRental.dto;

import com.ljuslin.wigellMCRental.entity.BikeStatus;
import com.ljuslin.wigellMCRental.entity.BookingStatus;
import com.ljuslin.wigellMCRental.service.BikeService;
import jakarta.validation.constraints.NotNull;

public record BookingPatchAdminDto(
        String status

        ) {
}
