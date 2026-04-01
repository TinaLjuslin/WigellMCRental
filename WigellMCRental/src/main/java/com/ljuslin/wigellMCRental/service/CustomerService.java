package com.ljuslin.wigellMCRental.service;

import com.ljuslin.wigellMCRental.dto.CustomerCreateDto;
import com.ljuslin.wigellMCRental.dto.CustomerResponseDto;
import com.ljuslin.wigellMCRental.dto.CustomerUpdateDto;
import jakarta.validation.Valid;

import java.util.List;

public interface CustomerService {
    CustomerResponseDto createCustomer(@Valid CustomerCreateDto dto);

    void deleteCustomer(Long id);

    List<CustomerResponseDto> getAllCustomers();

    CustomerResponseDto getCustomerById(Long id);

    CustomerResponseDto updateCustomer(Long id, @Valid CustomerUpdateDto dto);
}
