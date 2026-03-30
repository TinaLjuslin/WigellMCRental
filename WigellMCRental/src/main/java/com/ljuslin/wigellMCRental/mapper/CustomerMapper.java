package com.ljuslin.wigellMCRental.mapper;

import com.ljuslin.wigellMCRental.dto.AddressResponseDto;
import com.ljuslin.wigellMCRental.dto.CustomerCreateDto;
import com.ljuslin.wigellMCRental.dto.CustomerResponseDto;
import com.ljuslin.wigellMCRental.entity.Address;
import com.ljuslin.wigellMCRental.entity.Customer;

import java.util.List;

public final class CustomerMapper {
    public static CustomerResponseDto toDto(Customer customer) {
        List<AddressResponseDto> addressResponseDtos = customer.getAddresses().stream()
                .map(addr -> new AddressResponseDto(
                        addr.getId(),
                        addr.getStreet(),
                        addr.getZipCode(),
                        addr.getCity()))
                .toList();
        return
                new CustomerResponseDto(customer.getId(),
                        customer.getFirstName(),
                        customer.getLastName(),
                        customer.getEmail(),
                        customer.getUsername(),
                        addressResponseDtos,
                        customer.getKeycloakId());


    }

    public static Customer toEntityCreate(CustomerCreateDto dto, String keycloakId,
                                          List<Address> addresses) {
        return new Customer(
                null,
                dto.firstName(),
                dto.lastName(),
                dto.email(),
                dto.username(),
                addresses,
                keycloakId);

    }
}
