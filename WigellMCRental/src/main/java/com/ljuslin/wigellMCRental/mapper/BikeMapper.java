package com.ljuslin.wigellMCRental.mapper;


import com.ljuslin.wigellMCRental.dto.BikeResponseDto;
import com.ljuslin.wigellMCRental.dto.BikeCreateDto;
import com.ljuslin.wigellMCRental.entity.Bike;

public final class BikeMapper {

    public static BikeResponseDto toDto(Bike bike) {
        BikeResponseDto dto = new BikeResponseDto(
                bike.getId(),
                bike.getBrand(),
                bike.getEngineSize(),
                bike.getModel(),
                bike.getYear(),
                bike.getPricePerDay(),
                bike.getStatus().getSwedish()

        );
        return dto;
    }

    public static Bike toEntityCreate(BikeCreateDto dto) {
        Bike bike = new Bike(
                null,
                dto.brand(),
                dto.engineSize(),
                dto.model(),
                dto.year(),
                dto.pricePerDay());
        return bike;
    }
}
