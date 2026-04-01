package com.ljuslin.wigellMCRental.service;

import com.ljuslin.wigellMCRental.dto.BikeCreateDto;
import com.ljuslin.wigellMCRental.dto.BikeResponseDto;
import com.ljuslin.wigellMCRental.dto.BikeUpdateDto;
import com.ljuslin.wigellMCRental.entity.Bike;
import com.ljuslin.wigellMCRental.mapper.BikeMapper;

import java.time.LocalDate;
import java.util.List;

public interface BikeService {

    List<BikeResponseDto> getAllBikes();

    BikeResponseDto createBike(BikeCreateDto dto);

    List<BikeResponseDto> getAvailableBikes(LocalDate from, LocalDate to);

    void delete(Long id);

    BikeResponseDto updateBike(Long id, BikeUpdateDto dto);

    BikeResponseDto getBike(Long id);
}
