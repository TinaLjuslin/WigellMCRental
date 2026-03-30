package com.ljuslin.wigellMCRental.service;

import com.ljuslin.wigellMCRental.controller.BikeController;
import com.ljuslin.wigellMCRental.dto.BikeResponseDto;
import com.ljuslin.wigellMCRental.dto.BikeCreateDto;
import com.ljuslin.wigellMCRental.dto.BikeUpdateDto;
import com.ljuslin.wigellMCRental.entity.Bike;
import com.ljuslin.wigellMCRental.exception.ItemNotFoundException;
import com.ljuslin.wigellMCRental.mapper.BikeMapper;
import com.ljuslin.wigellMCRental.repository.BikeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class BikeServiceImpl implements BikeService {
    private static final Logger logger = LoggerFactory.getLogger(BikeServiceImpl.class);
    private final BikeRepository bikeRepository;

    public BikeServiceImpl(BikeRepository bikeRepository) {
        this.bikeRepository = bikeRepository;
    }

    public List<BikeResponseDto> getAllBikes() {
        List<Bike> bikes = bikeRepository.findAll();
        return bikes.stream()
                .map(BikeMapper::toDto)
                .toList();
    }

    public BikeResponseDto createBike(BikeCreateDto dto) {
        Bike bike = bikeRepository.save(BikeMapper.toEntityCreate(dto));
        logger.info("admin created bike {}", bike.getId());
        return BikeMapper.toDto(bike);
    }

    public List<BikeResponseDto> getAvailableBikes(LocalDate from, LocalDate to) {
        List<Bike> availableBikes = bikeRepository.findAvailableBikes(from, to);

        logger.info("Found {} available bikes for period {} to {}", availableBikes.size(), from, to);

        return availableBikes.stream()
                .map(BikeMapper::toDto)
                .toList();
    }

    public void delete(Long id) {
        var s = bikeRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Motorcykel med id " + id + "finns " +
                        "inte "));
        bikeRepository.delete(s);
        logger.info("admin deleted bike {}", id);
    }

    public BikeResponseDto updateBike(Long id, BikeUpdateDto dto) {
        // 1. Hitta den befintliga (eller kasta fel om den inte finns)
        Bike bike = bikeRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Motorcykel med id " + id + "finns " +
                        "inte"));

        // 2. Ersätt alla fält (Fullständig PUT)
        bike.setBrand(dto.brand());
        bike.setModel(dto.model());
        bike.setEngineSize(dto.engineSize());
        bike.setYear(dto.year());
        bike.setPricePerDay(dto.pricePerDay());
        bike.setStatus(dto.status());

        // 3. Spara
        Bike updatedBike = bikeRepository.save(bike);

        // 4. Logga (VG-krav: vem gjorde vad)
        logger.info("Admin updated bike ID {}: brand={}, model={}, status={}",
                id, dto.brand(), dto.model(), dto.status());

        return BikeMapper.toDto(updatedBike);
    }

    public BikeResponseDto getBike(Long id) {
        return BikeMapper.toDto(bikeRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Motorcykel med id " + id + " finns inte")));
    }
}

