package com.ljuslin.wigellMCRental.controller;

import com.ljuslin.wigellMCRental.dto.CustomerCreateDto;
import com.ljuslin.wigellMCRental.dto.CustomerResponseDto;
import com.ljuslin.wigellMCRental.dto.CustomerUpdateDto;
import com.ljuslin.wigellMCRental.service.CustomerService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {
    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<CustomerResponseDto>> getAllCustomers() {
        logger.debug("Admin retrieving all customers");

            List<CustomerResponseDto> customers = customerService.getAllCustomers();
            return ResponseEntity.ok(customers);


    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CustomerResponseDto> createCustomer(@Valid @RequestBody CustomerCreateDto dto) {
        logger.debug("Admin creating customer");
        CustomerResponseDto customerDto = customerService.createCustomer(dto);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(customerDto.id())
                .toUri();

        return ResponseEntity.created(location).body(customerDto);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<CustomerResponseDto> getCustomer(@PathVariable long id) {
        logger.debug("Admin retrieving customer with id {} ", id);
        CustomerResponseDto customer = customerService.getCustomerById(id);
        return ResponseEntity.ok(customer);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        logger.debug("Admin deleting customer with id {}", id);
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CustomerResponseDto> updateCustomer(@PathVariable Long id,
                                                              @RequestBody @Valid CustomerUpdateDto dto) {
    logger.debug("Admin updating customer with id: {}", id);
        return ResponseEntity.ok(customerService.updateCustomer(id, dto));
    }
}