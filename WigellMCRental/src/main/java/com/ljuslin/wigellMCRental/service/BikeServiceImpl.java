package com.ljuslin.wigellMCRental.service;

import com.ljuslin.wigellMCRental.dto.BikeResponseDto;
import com.ljuslin.wigellMCRental.dto.BikeCreateDto;
import com.ljuslin.wigellMCRental.dto.BikeUpdateDto;
import com.ljuslin.wigellMCRental.entity.Bike;
import com.ljuslin.wigellMCRental.entity.BikeStatus;
import com.ljuslin.wigellMCRental.exception.IllegalActionException;
import com.ljuslin.wigellMCRental.exception.ItemNotFoundException;
import com.ljuslin.wigellMCRental.mapper.BikeMapper;
import com.ljuslin.wigellMCRental.repository.BikeRepository;
import com.ljuslin.wigellMCRental.repository.BookingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class BikeServiceImpl implements BikeService {
    private static final Logger logger = LoggerFactory.getLogger(BikeServiceImpl.class);
    private final BikeRepository bikeRepository;
    private final BookingRepository bookingRepository;

    public BikeServiceImpl(BikeRepository bikeRepository, BookingRepository bookingRepository) {
        this.bikeRepository = bikeRepository;
        this.bookingRepository = bookingRepository;
    }

    public List<BikeResponseDto> getAllBikes() {
        List<Bike> bikes = bikeRepository.findAll();
        logger.info("{} bikes retrieved", bikes.size());
        return bikes.stream()
                .map(BikeMapper::toDto)
                .toList();
    }

    public BikeResponseDto createBike(BikeCreateDto dto) {
        Bike bike = bikeRepository.save(BikeMapper.toEntityCreate(dto));
        logger.info("New bike created: {} {}, with id {}", bike.getBrand(), bike.getModel()
                , bike.getId());
        return BikeMapper.toDto(bike);
    }

    public List<BikeResponseDto> getAvailableBikes(LocalDate from, LocalDate to) {
        List<Bike> bikes = bikeRepository.findAvailableBikes(from, to);
        logger.info("{} available bikes retrieved", bikes.size());
        return bikes.stream()
                .map(BikeMapper::toDto)
                .toList();
    }

    public void delete(Long id) {
        Bike bike = bikeRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Motorcykel med id " + id + " " +
                        " hittades inte."));

        boolean hasFutureBookings = bookingRepository.bikeHasActiveFutureBookings(bike,
                LocalDate.now());

        if (hasFutureBookings) {
            throw new IllegalActionException("Kan inte radera MC med id " + id + " då den har " +
                    "framtida bokningar. Ta bort dessa bokningar först.");
        }
        bike.setStatus(BikeStatus.DELETED);
        bikeRepository.save(bike);
        logger.info("Bike with id {} is set to status deleted", id);
    }

    public BikeResponseDto updateBike(Long id, BikeUpdateDto dto) {
        Bike bike = bikeRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Motorcykel med id " + id +
                        " hittades inte."));
        bike.setBrand(dto.brand());
        bike.setModel(dto.model());
        bike.setEngineSize(dto.engineSize());
        bike.setYear(dto.year());
        bike.setPricePerDay(dto.pricePerDay());
        bike.setStatus(dto.status());

        Bike updatedBike = bikeRepository.save(bike);

        logger.info("Updated bike ID {}: brand={}, model={}, status={}",
                id, dto.brand(), dto.model(), dto.status());

        return BikeMapper.toDto(updatedBike);
    }

    public BikeResponseDto getBike(Long id) {
        Bike bike = bikeRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Motorcykel med id " + id +
                        " hittades inte"));
        logger.info("Bike with id {} retrieved");
        return BikeMapper.toDto(bike);
    }
}

