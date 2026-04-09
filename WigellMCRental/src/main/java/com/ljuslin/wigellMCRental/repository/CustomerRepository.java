package com.ljuslin.wigellMCRental.repository;

import com.ljuslin.wigellMCRental.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer,Long> {
    Optional<Customer> findByKeycloakId(String keycloakId);
    Optional<Customer> findByUsername(String username);
    boolean existsByEmail(String email);
}
