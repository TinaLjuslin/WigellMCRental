package com.ljuslin.wigellMCRental.mapper;

import com.ljuslin.wigellMCRental.dto.AddressResponseDto;
import com.ljuslin.wigellMCRental.dto.AddressCreateDto;
import com.ljuslin.wigellMCRental.entity.Address;
import com.ljuslin.wigellMCRental.entity.Customer;

public final class AddressMapper {
    public static AddressResponseDto toDto(Address address) {
        return new AddressResponseDto(
                address.getId(),
                address.getStreet(),
                address.getZipCode(),
                address.getCity());
    }
    public static Address toEntityCreate(AddressCreateDto dto, Customer customer) {
        return new Address(
                null,
                dto.street(),
                dto.zipCode(),
                dto.city(),
                customer);
    }
}
