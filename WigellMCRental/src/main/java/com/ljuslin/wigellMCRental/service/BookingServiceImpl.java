package com.ljuslin.wigellMCRental.service;

import com.ljuslin.wigellMCRental.dto.BookingCreateDto;
import com.ljuslin.wigellMCRental.dto.BookingResponseDto;
import com.ljuslin.wigellMCRental.dto.ConversionResponse;
import com.ljuslin.wigellMCRental.entity.Bike;
import com.ljuslin.wigellMCRental.entity.Booking;
import com.ljuslin.wigellMCRental.entity.Customer;
import com.ljuslin.wigellMCRental.exception.IllegalActionException;
import com.ljuslin.wigellMCRental.exception.IllegalDataException;
import com.ljuslin.wigellMCRental.exception.ItemNotFoundException;
import com.ljuslin.wigellMCRental.mapper.BookingMapper;
import com.ljuslin.wigellMCRental.repository.BikeRepository;
import com.ljuslin.wigellMCRental.repository.BookingRepository;
import com.ljuslin.wigellMCRental.repository.CustomerRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final CustomerRepository customerRepository;
    private final BikeRepository bikeRepository;
    @Value("${services.currency-converter.url}")
    private String currencyServiceUrl;
    private final RestTemplate restTemplate;

    public BookingServiceImpl(BookingRepository bookingRepository,
                              CustomerRepository customerRepository,
                              BikeRepository bikeRepository, RestTemplate restTemplate) {
        this.bookingRepository = bookingRepository;
        this.customerRepository = customerRepository;
        this.bikeRepository = bikeRepository;
        this.restTemplate = restTemplate;
    }

    @Transactional
    public BookingResponseDto createBooking(BookingCreateDto dto) {

        Customer customer = customerRepository.findById(dto.customerId())
                .orElseThrow(() -> new ItemNotFoundException("Customer not found"));

        Bike bike = bikeRepository.findById(dto.bikeId())
                .orElseThrow(() -> new ItemNotFoundException("Bike not found"));
        if (bookingRepository.existsOverlappingBooking(bike.getId(), dto.startDate(), dto.endDate())) {
            throw new IllegalActionException("Motorcykeln är tyvärr redan bokad under denna " +
                    "period.");
        }
        int days = getDaysBetweenDates(dto.startDate(), dto.endDate());
        BigDecimal totalPriceSek = bike.getPricePerDay().multiply(BigDecimal.valueOf(days));

        BigDecimal totalPriceGbp = convertSekToGbp(totalPriceSek);

        // Skapa och spara bokningen
        Booking booking = BookingMapper.toEntityCreate(dto, customer, bike, totalPriceSek, totalPriceGbp);

        return BookingMapper.toDto(bookingRepository.save(booking));
    }


    private int getDaysBetweenDates(LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) throw new IllegalDataException("Startdatum måste vara " +
                "före slutdatum");
        int days = Math.toIntExact(ChronoUnit.DAYS.between(startDate, endDate));
        if (days <= 0) return 1; //om start och slut är samma dag, hyra en dag
        return days;
    }

    private BigDecimal convertSekToGbp(BigDecimal sekAmount) {
        String url = String.format("%s?amount=%s&from=%s&to=%s",
                currencyServiceUrl, sekAmount.toPlainString(), "SEK", "GBP");
        try {
            // RestTemplate anropar din kompis på port 8586
            ConversionResponse response = restTemplate.getForObject(url, ConversionResponse.class);

            if (response != null && response.to() != null) {
                return response.to().amount(); // Här får vi ut det konverterade beloppet!
            }
        } catch (Exception e) {
            // Om hans tjänst är nere eller något går fel
            System.err.println("Kunde inte nå valutatjänsten: " + e.getMessage());
            // För VG bör du nog kasta ett exception här så bokningen inte sparas med fel pris
            throw new RuntimeException("Valutakonvertering misslyckades.");
        }
        return BigDecimal.ZERO;
    }

}
