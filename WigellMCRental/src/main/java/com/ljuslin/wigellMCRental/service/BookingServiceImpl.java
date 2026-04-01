package com.ljuslin.wigellMCRental.service;

import com.ljuslin.wigellMCRental.dto.*;
import com.ljuslin.wigellMCRental.entity.*;
import com.ljuslin.wigellMCRental.exception.DataConflictException;
import com.ljuslin.wigellMCRental.exception.IllegalActionException;
import com.ljuslin.wigellMCRental.exception.IllegalDataException;
import com.ljuslin.wigellMCRental.exception.ItemNotFoundException;
import com.ljuslin.wigellMCRental.mapper.BookingMapper;
import com.ljuslin.wigellMCRental.repository.BikeRepository;
import com.ljuslin.wigellMCRental.repository.BookingRepository;
import com.ljuslin.wigellMCRental.repository.CustomerRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class BookingServiceImpl implements BookingService {
    private static final Logger logger = LoggerFactory.getLogger(BookingServiceImpl.class);
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
                .orElseThrow(() -> new ItemNotFoundException("Kund med id " + dto.customerId() +
                        " hittades inte."));

        Bike bike = bikeRepository.findById(dto.bikeId())
                .orElseThrow(() -> new ItemNotFoundException("Mc med id " + dto.bikeId() +
                        "hittades inte."));
        if (bike.getStatus() != BikeStatus.AVAILABLE) {
            throw new IllegalActionException("Motorcykeln kan inte bokas (Status: " +
                    bike.getStatus().getSwedish() + ")");
        }
        overlappingBooking(bike.getId(), dto.startDate(), dto.endDate());
        int days = getDaysBetweenDates(dto.startDate(), dto.endDate());
        BigDecimal totalPriceSek = bike.getPricePerDay().multiply(BigDecimal.valueOf(days));

        BigDecimal totalPriceGbp = convertSekToGbp(totalPriceSek);

        Booking booking = BookingMapper.toEntityCreate(dto, customer, bike, totalPriceSek, totalPriceGbp);
        booking = bookingRepository.save(booking);
        logger.info("Booking with id {} created", booking.getId());
        return BookingMapper.toDto(booking);
    }

    @Transactional
    public BookingResponseDto userPatchBooking(Long bookingId, BookingPatchUserDto dto) {
        Booking savedBooking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ItemNotFoundException("Bokning med id " + bookingId +
                        "hittades inte."));

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String keycloakId = "";

        if (principal instanceof Jwt jwt) {
            keycloakId = jwt.getSubject();
        }
        var auth = SecurityContextHolder.getContext().getAuthentication();

        if (!savedBooking.getCustomer().getKeycloakId().equals(keycloakId)) {
            throw new IllegalActionException("Du kan endast ändra dina egna bokningar.");
        }
        if (dto.bikeId() != null) {
            Bike newBike = bikeRepository.findById(dto.bikeId())
                    .orElseThrow(() -> new ItemNotFoundException("Motorcykel med id " + dto.bikeId() + " hittades inte."));
            if (newBike.getStatus() != BikeStatus.AVAILABLE) {
                throw new IllegalActionException("Motorcykeln kan inte bokas (Status: " + newBike.getStatus().getSwedish() + ")");
            }
            savedBooking.setBike(newBike);
        }
        LocalDate finalStart = (dto.startDate() != null) ? dto.startDate() : savedBooking.getStartDate();
        LocalDate finalEnd = (dto.endDate() != null) ? dto.endDate() : savedBooking.getEndDate();

        overlappingBooking(savedBooking.getBike().getId(), finalStart, finalEnd, bookingId);
        savedBooking.setStartDate(finalStart);
        savedBooking.setEndDate(finalEnd);
        int days = getDaysBetweenDates(finalStart, finalEnd);
        BigDecimal totalPriceSek =
                savedBooking.getBike().getPricePerDay().multiply(BigDecimal.valueOf(days));

        BigDecimal totalPriceGbp = convertSekToGbp(totalPriceSek);
        savedBooking.setTotalPriceSek(totalPriceSek);
        savedBooking.setTotalPriceGbp(totalPriceGbp);
        savedBooking = bookingRepository.save(savedBooking);
        logger.info("Booking with id {} updated bu user", savedBooking.getId());
        return BookingMapper.toDto(savedBooking);

    }

    @Transactional
    public BookingResponseDto adminPatchBooking(Long id, BookingPatchAdminDto dto) {
        Booking savedBooking = bookingRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Bokning med id " + id + " hittades " +
                        "inte."));

        if (dto.status() != null) {
            try {
                BookingStatus newStatus = BookingStatus.valueOf(dto.status().toUpperCase());
                savedBooking.setStatus(newStatus);
            } catch (IllegalArgumentException e) {
                throw new IllegalDataException("Ogiltig status. Tillåtna värden: BOOKED, RETURNED, CANCELLED");
            }
        }
        savedBooking = bookingRepository.save(savedBooking);
        logger.info("Booking with id {} updated by admin", savedBooking.getId());
        return BookingMapper.toDto(savedBooking);
    }

    private List<Booking> getBookings() {
        List<Booking> bookings = bookingRepository.findAll();
        logger.info("{} bookings retrieved", bookings.size());
        return bookings;
    }


    public BookingResponseDto getBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Bokning med id " + id + " hittades" +
                        " inte."));
        logger.info("Booking with id {} retrieved", booking.getId());
        return BookingMapper.toDto(booking);
    }

    @Transactional
    public BookingResponseDto updateBooking(Long id, BookingUpdateDto dto) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Bokning med id " + id + " hittades " +
                        "inte"));
        Customer customer = customerRepository.findById(dto.customerId())
                .orElseThrow(() -> new ItemNotFoundException("Kund med id " + dto.customerId() +
                        " hittades inte."));

        Bike bike = bikeRepository.findById(dto.bikeId())
                .orElseThrow(() -> new ItemNotFoundException("Mc med id " + dto.bikeId() + " " +
                        "hittades inte."));
        if (bike.getStatus() != BikeStatus.AVAILABLE) {
            throw new IllegalActionException("Motorcykeln är för tillfället " + bike.getStatus().getSwedish());
        }
        overlappingBooking(bike.getId(), dto.startDate(), dto.endDate(), booking.getId());
        int days = getDaysBetweenDates(dto.startDate(), dto.endDate());
        BigDecimal totalPriceSek = bike.getPricePerDay().multiply(BigDecimal.valueOf(days));

        BigDecimal totalPriceGbp = convertSekToGbp(totalPriceSek);

        booking.setBike(bike);
        booking.setCustomer(customer);
        booking.setStartDate(dto.startDate());
        booking.setEndDate(dto.endDate());
        booking.setTotalPriceSek(totalPriceSek);
        booking.setTotalPriceGbp(totalPriceGbp);
        booking = bookingRepository.save(booking);
        logger.info("Booking with id {} updated", booking.getId());
        return BookingMapper.toDto(booking);
    }

    public void deleteBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Bokning med id " + id + " hittades " +
                        "inte "));
        if (booking.getStatus() == BookingStatus.RETURNED) {
            throw new IllegalActionException("Kan inte avboka en redan avslutad bokning.");
        }
        if (booking.getStartDate().isBefore(LocalDate.now())) {
            throw new IllegalActionException("Startdatumet har passerat. Bokningen kan inte avbokas.");
        }
        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
        logger.info("Booking with id {} is cancelled", id);
    }
    private List<Booking> getBookingsForUser(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Kund med id " + id +
                        " hittades inte."));

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String keycloakId = "";

        if (principal instanceof Jwt jwt) {
            keycloakId = jwt.getSubject();
        }
        var auth = SecurityContextHolder.getContext().getAuthentication();

        if (!customer.getKeycloakId().equals(keycloakId)) {
            throw new IllegalActionException("Du kan endast ändra dina egna bokningar.");
        }

        List<Booking> bookings = bookingRepository.findBookingsByCustomerId(id);
        logger.info("{} bookings retrieved for customer {}", bookings.size(), id);
        return bookings;
    }
    public List<BookingResponseDto> getAllOrFiltered(Long id) {
        List<Booking> bookings;
        if (id == null) {
            if (!isAdmin()) throw new IllegalActionException("Bara admin kan hämta alla användare");
            bookings = getBookings();
        } else {
            //detta borde vara user, skicka vidare, user kollas i nästa metod
            bookings = getBookingsForUser(id);
        }
        return bookings.stream()
                .map(BookingMapper::toDto)
                .toList();
    }
    private void overlappingBooking(Long bikeId, LocalDate startDate, LocalDate endDate) {
        if (bookingRepository.isBikeOccupied(bikeId, startDate, endDate)) {
            throw new DataConflictException("Motorcykeln är tyvärr redan bokad under denna " +
                    "period.");
        }
    }

    private void overlappingBooking(Long bikeId, LocalDate startDate, LocalDate endDate,
                                    Long bookingId) {
        if (bookingRepository.isBikeOccupiedExcludingSelf(bikeId, bookingId, startDate,
                endDate)) {
            throw new DataConflictException("Motorcykeln är tyvärr redan bokad under denna " +
                    "period.");
        }
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
            ConversionResponse response = restTemplate.getForObject(url, ConversionResponse.class);

            if (response != null && response.to() != null) {
                return response.to().amount();
            }
        } catch (Exception e) {
            System.err.println("Kunde inte nå valutatjänsten: " + e.getMessage());
            throw new RuntimeException("Valutakonvertering misslyckades.");
        }
        throw new RuntimeException("Valutakonvertering misslyckades.");

    }
    private boolean isAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return false;

        // Vi letar efter ROLE_ADMIN i användarens authorities (roller)
        return auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_ADMIN"));
    }
}
