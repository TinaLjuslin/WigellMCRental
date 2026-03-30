package com.ljuslin.wigellMCRental.service;

import com.ljuslin.wigellMCRental.dto.AddressCreateDto;
import com.ljuslin.wigellMCRental.dto.AddressResponseDto;

public interface AddressService {
    AddressResponseDto addAddress(Long id, AddressCreateDto cto);
    void removeAddress(Long customerId, Long addressId);
}
