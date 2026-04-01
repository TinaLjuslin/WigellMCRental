package com.ljuslin.wigellMCRental.config;

import com.ljuslin.wigellMCRental.dto.AddressCreateDto;
import com.ljuslin.wigellMCRental.dto.BookingCreateDto;
import com.ljuslin.wigellMCRental.dto.CustomerCreateDto;
import com.ljuslin.wigellMCRental.entity.Bike;
import com.ljuslin.wigellMCRental.repository.BikeRepository;
import com.ljuslin.wigellMCRental.repository.BookingRepository;
import com.ljuslin.wigellMCRental.repository.CustomerRepository;
import com.ljuslin.wigellMCRental.service.BookingService;
import com.ljuslin.wigellMCRental.service.BookingServiceImpl;
import com.ljuslin.wigellMCRental.service.CustomerService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initData(BikeRepository bikeRepository,
                               CustomerService customerService,
                               CustomerRepository customerRepository,
                               BookingServiceImpl bookingService,
                               BookingRepository bookingRepository) {
        return args -> {
            if (bikeRepository.count() == 0) {
                bikeRepository.saveAll(List.of(
                        new Bike(null, "Harley-Davidson", "1200.0", "Iron 883", 1975, BigDecimal.valueOf(200.0)),
                        new Bike(null, "Ducati", "1500.0", "Monster", 1985,
                                BigDecimal.valueOf(300)),
                        new Bike(null, "Kawasaki", "900.0", "Ninja 650", 1995,
                                BigDecimal.valueOf(400)),
                        new Bike(null, "Yamaha", "1000.0", "MT-07", 2005,
                                BigDecimal.valueOf(350)),
                        new Bike(null, "Honda", "950.0", "CB650R", 2015, BigDecimal.valueOf(280))
                ));
            }
            if (customerRepository.count() == 0) {
                try {
                    customerService.createCustomer(new CustomerCreateDto(
                            "Erik", "Ljuslin", "erik@test.se", "erik_biker", "Lösenord123!",
                            List.of(new AddressCreateDto("Gatan 1", "12345", "Stockholm"))
                    ));

                    customerService.createCustomer(new CustomerCreateDto(
                            "Anna", "Andersson", "anna@test.se", "anna_biker", "Lösenord123!",
                            List.of(new AddressCreateDto("Vägen 2", "54321", "Göteborg"))
                    ));

                    customerService.createCustomer(new CustomerCreateDto(
                            "Bettan", "Karlsson", "bettan@test.se", "bettan_b", "Lösenord123!",
                            List.of(new AddressCreateDto("Vägen 3", "54321", "Göteborg"))
                    ));

                    customerService.createCustomer(new CustomerCreateDto(
                            "Ceasar", "Carlsson", "c@test.se", "harley_ceasar", "Lösenord123!",
                            List.of(new AddressCreateDto("Vägen 3", "54321", "Göteborg"))
                    ));
                    customerService.createCustomer(new CustomerCreateDto(
                            "Fredrik", "Fjärnsson", "freddan@test.se", "freddan_mc", "Lösenord123!",
                            List.of(new AddressCreateDto("Vägen 3", "54321", "Göteborg"))
                    ));
                    System.out.println(">> 5 Real customers created in Keycloak and DB!");
                } catch (Exception e) {
                    System.out.println(">> Note: Could not create init-users. They might already exist in Keycloak.");
                }

            }
            if (bookingRepository.count() == 0) {
                bookingService.createBooking(new BookingCreateDto(1L,2L, LocalDate.of(2026,06,01),
                        LocalDate.of(2026,06,10)));
                bookingService.createBooking(new BookingCreateDto(2L,1L, LocalDate.of(2026,06,01),
                        LocalDate.of(2026,06,10)));
                bookingService.createBooking(new BookingCreateDto(2L,2L, LocalDate.of(2026,07,01),
                        LocalDate.of(2026,07,10)));
                bookingService.createBooking(new BookingCreateDto(2L,1L, LocalDate.of(2026,07,01),
                        LocalDate.of(2026,07,10)));
                bookingService.createBooking(new BookingCreateDto(3L,3L, LocalDate.of(2026,07,01),
                        LocalDate.of(2026,07,10)));


            }

        };
    }
}