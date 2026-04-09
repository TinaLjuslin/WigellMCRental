package com.ljuslin.wigellMCRental.service;

import com.ljuslin.wigellMCRental.dto.AddressCreateDto;
import com.ljuslin.wigellMCRental.dto.CustomerCreateDto;
import com.ljuslin.wigellMCRental.dto.CustomerResponseDto;
import com.ljuslin.wigellMCRental.dto.CustomerUpdateDto;
import com.ljuslin.wigellMCRental.entity.Address;
import com.ljuslin.wigellMCRental.entity.Customer;
import com.ljuslin.wigellMCRental.exception.DataConflictException;
import com.ljuslin.wigellMCRental.exception.IllegalActionException;
import com.ljuslin.wigellMCRental.exception.ItemNotFoundException;
import com.ljuslin.wigellMCRental.mapper.CustomerMapper;
import com.ljuslin.wigellMCRental.repository.AddressRepository;
import com.ljuslin.wigellMCRental.repository.BookingRepository;
import com.ljuslin.wigellMCRental.repository.CustomerRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class CustomerServiceImpl implements CustomerService {
    private static final Logger logger = LoggerFactory.getLogger(CustomerServiceImpl.class);
    private final AddressRepository addressRepository;
    private final CustomerRepository customerRepository;
    private final BookingRepository bookingRepository;
    private final KeycloakService keycloakService;

    public CustomerServiceImpl(AddressRepository addressRepository,
                               CustomerRepository customerRepository,
                              BookingRepository bookingRepository,
                               KeycloakService keycloakService) {
        this.addressRepository = addressRepository;
        this.customerRepository = customerRepository;
        this.bookingRepository = bookingRepository;
        this.keycloakService = keycloakService;
    }

    @Transactional
    public CustomerResponseDto createCustomer(@Valid CustomerCreateDto dto) {
        String kcId = keycloakService.createKeycloakUser(dto);
        Customer customer = CustomerMapper.toEntityCreate(dto, kcId, new ArrayList<>());

        if (dto.address() != null) {
            for (AddressCreateDto addrDto : dto.address()) {
                Address address = new Address(
                        null,
                        addrDto.street(),
                        addrDto.zipCode(),
                        addrDto.city(),
                        customer
                );
                customer.addAddress(address);
            }
        }

        Customer saved = customerRepository.save(customer);

        logger.debug("admin created customer {}", saved.getId());

        return CustomerMapper.toDto(saved);
    }

    @Override
    @Transactional
    public void deleteCustomer(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Customer not found with id: " + id));
//ska man kolla om customer har några pågående bokninger
        boolean hasFutureBookings = bookingRepository.customerHasActiveFutureBookings(customer,
                LocalDate.now());

        if (hasFutureBookings) {
            throw new IllegalActionException("Kan inte radera kund med id " + id + " då den har " +
                    "framtida bokningar. Ta bort dessa bokningar först.");
    }

        keycloakService.deleteKeycloakUser(customer.getKeycloakId());
        customer.setFirstName("deleted");
        customer.setLastName("deleted");
        customer.setEmail("deleted_" + customer.getId() + "@mcrental.se");
        customer.getAddresses().clear();
        customer.setUsername("deleted");
        customer.setKeycloakId("deleted_" + id);
        //customerRepository.delete(customer);
        customerRepository.save(customer);
        logger.debug("Customer with id {} is now soft deleted", id);
    }

    public List<CustomerResponseDto> getAllCustomers() {
        List<Customer> customers = customerRepository.findAll();
        logger.debug("{} customers retrieved", customers.size());
        return customers.stream()
                .map(CustomerMapper::toDto)
                .toList();
    }

    public CustomerResponseDto getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id).orElseThrow(() ->
                new ItemNotFoundException("Customer med id " + id + " hittades inte"));
        logger.debug("Customer with id {} retrieved", id);
        return CustomerMapper.toDto(customer);


    }

    public CustomerResponseDto updateCustomer(Long id, @Valid CustomerUpdateDto dto) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Customer not found with id: " + id));
        if (!customer.getEmail().equalsIgnoreCase(dto.email()) &&
                customerRepository.existsByEmail(dto.email())) {
            throw new DataConflictException("Email already in use by another customer");
        }
        keycloakService.updateKeycloakUser(customer.getKeycloakId(), dto);
        customer.setFirstName(dto.firstName());
        customer.setLastName(dto.lastName());
        customer.setEmail(dto.email());
        customerRepository.save(customer);
        logger.debug("Customer with id {} updated.", id);
        return CustomerMapper.toDto(customer);
    }
}
