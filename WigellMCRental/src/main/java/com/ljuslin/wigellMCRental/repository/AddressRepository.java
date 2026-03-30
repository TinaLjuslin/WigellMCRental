package com.ljuslin.wigellMCRental.repository;

import com.ljuslin.wigellMCRental.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address,Long> {
    Optional<Address> findByStreetAndZipCodeAndCity(String street, String zipCode, String city);
}
