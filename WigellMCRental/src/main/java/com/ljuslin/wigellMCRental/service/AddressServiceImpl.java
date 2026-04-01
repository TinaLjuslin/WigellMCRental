package com.ljuslin.wigellMCRental.service;

import com.ljuslin.wigellMCRental.controller.BookingController;
import com.ljuslin.wigellMCRental.dto.AddressCreateDto;
import com.ljuslin.wigellMCRental.dto.AddressResponseDto;
import com.ljuslin.wigellMCRental.entity.Address;
import com.ljuslin.wigellMCRental.entity.Customer;
import com.ljuslin.wigellMCRental.exception.DataConflictException;
import com.ljuslin.wigellMCRental.exception.ItemNotFoundException;
import com.ljuslin.wigellMCRental.mapper.AddressMapper;
import com.ljuslin.wigellMCRental.repository.AddressRepository;
import com.ljuslin.wigellMCRental.repository.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AddressServiceImpl implements AddressService {
    private static final Logger logger = LoggerFactory.getLogger(AddressServiceImpl.class);
    private final AddressRepository addressRepository;
    private final CustomerRepository customerRepository;

    public AddressServiceImpl(AddressRepository addressRepository, CustomerRepository customerRepository) {
        this.addressRepository = addressRepository;
        this.customerRepository = customerRepository;
    }

    public AddressResponseDto addAddress(Long customerId, AddressCreateDto dto) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ItemNotFoundException("Customer not found"));
        boolean addressExists = customer.getAddresses().stream()
                .anyMatch(a -> a.getStreet().equalsIgnoreCase(dto.street()) &&
                        a.getZipCode().equals(dto.zipCode()) &&
                        a.getCity().equalsIgnoreCase(dto.city()));

        if (addressExists) {
            throw new DataConflictException("This address is already registered for this customer");
        }
        Address address = new Address(null, dto.street(), dto.zipCode(), dto.city(), customer);
        address = addressRepository.save(address);
        logger.info("New address has been saved successfully with id {}", address.getId());
        return AddressMapper.toDto(address);
    }
    public void removeAddress(Long customerId, Long addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ItemNotFoundException("Adress med id : " + addressId +
                        " hittades inte."));

        if (!address.getCustomer().getId().equals(customerId)) {
            throw new DataConflictException("Adress med id " + addressId + " tillhör inte " +
                    "kund med id " + customerId);
        }

        address.getCustomer().getAddresses().remove(address);

        addressRepository.delete(address);
        logger.info("Address with id {} has been removed.", address.getId());
    }
}
