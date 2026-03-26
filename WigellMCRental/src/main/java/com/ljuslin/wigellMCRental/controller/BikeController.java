package com.ljuslin.wigellMCRental.controller;

import com.ljuslin.wigellMCRental.entity.Bike;
import com.ljuslin.wigellMCRental.service.BikeService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/bikes")
public class BikeController {
private final BikeService bikeService;

    public BikeController(BikeService bikeService) {
        this.bikeService = bikeService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<Bike> getAllBikes() {
        return bikeService.getAllBikes();
    }
}
