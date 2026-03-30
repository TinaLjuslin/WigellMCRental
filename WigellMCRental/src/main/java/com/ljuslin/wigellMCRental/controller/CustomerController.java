package com.ljuslin.wigellMCRental.controller;

import com.ljuslin.wigellMCRental.dto.CustomerCreateDto;
import com.ljuslin.wigellMCRental.dto.CustomerResponseDto;
import com.ljuslin.wigellMCRental.dto.CustomerUpdateDto;
import com.ljuslin.wigellMCRental.entity.Customer;
import com.ljuslin.wigellMCRental.service.CustomerService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
/* ADMIN:
X Lista kunder GET /api/v1/customers
X Hämta kund GET /api/v1/customers/{customerId}
X Lägga till kund POST /api/v1/customers
X Ta bort kund DELETE /api/v1/customers/{customerId}
• Uppdatera kund PUT /api/v1/customers/{customerId}
Lägga till adress POST /api/v1/customers/{customerId}/addresses
• Ta bort adress DELETE /api/v1/customers/{customerId}/addresses/{addressId
USER:

*/


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
        logger.info("Admin retrieveing all customers");

        try {
            List<CustomerResponseDto> customers = customerService.getAllCustomers();
            logger.debug("Successfully retrieved {} customers", customers.size());
            return ResponseEntity.ok(customers);
        } catch (Exception e) {
            logger.error("Failed to retrieve customers: {}", e.getMessage());
            throw e;
        }


    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CustomerResponseDto> createCustomer(@Valid @RequestBody CustomerCreateDto dto) {
        logger.info("Admin creating customer");
        return new ResponseEntity<>(customerService.createCustomer(dto), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<CustomerResponseDto> getCustomer(@PathVariable long id) {
        try {
            CustomerResponseDto customer = customerService.getCustomerById(id);
            logger.debug("Successfully retrieved customer with id ", id);
            return ResponseEntity.ok(customer);
        } catch (Exception e) {
            logger.error("Failed to retrieve customer ", e.getMessage());
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        logger.info("Admin deleted customer with id {}", id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CustomerResponseDto> updateCustomer(@PathVariable Long id,
                                                              @RequestBody @Valid CustomerUpdateDto dto) {
        return ResponseEntity.ok(customerService.updateCustomer(id, dto));
    }
}