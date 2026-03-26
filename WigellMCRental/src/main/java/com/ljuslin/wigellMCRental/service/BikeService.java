package com.ljuslin.wigellMCRental.service;

import com.ljuslin.wigellMCRental.entity.Bike;
import com.ljuslin.wigellMCRental.repository.BikeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BikeService {
    private final BikeRepository bikeRepository;

    public BikeService(BikeRepository bikeRepository) {
        this.bikeRepository = bikeRepository;
    }
    public List<Bike> getAllBikes() {
        return bikeRepository.findAll();
    }
}
