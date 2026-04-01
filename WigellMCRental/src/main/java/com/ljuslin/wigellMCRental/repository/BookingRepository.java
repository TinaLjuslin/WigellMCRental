package com.ljuslin.wigellMCRental.repository;

import com.ljuslin.wigellMCRental.entity.Booking;
import com.ljuslin.wigellMCRental.entity.Bike;
import com.ljuslin.wigellMCRental.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("""
            SELECT COUNT(b) > 0 FROM Booking b
                        WHERE b.bike.id = :bikeId
                        AND b.status <> com.ljuslin.wigellMCRental.entity.BookingStatus.CANCELLED
                        AND b.startDate <= :endDate
                        AND b.endDate >= :startDate""")
    boolean isBikeOccupied(
            @Param("bikeId") Long bikeId,
            @Param("startDate") LocalDate start,
            @Param("endDate") LocalDate end
    );

    @Query("""
            SELECT COUNT(b) > 0 FROM Booking b
                        WHERE b.bike.id = :bikeId
                        AND b.status <> com.ljuslin.wigellMCRental.entity.BookingStatus.CANCELLED  
                        AND b.id != :currentBookingId 
                        AND b.startDate <= :endDate
                        AND b.endDate >= :startDate""")
    boolean isBikeOccupiedExcludingSelf(
            @Param("bikeId") Long bikeId,
            @Param("currentBookingId") Long currentBookingId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    List<Booking> findBookingsByCustomerId(Long customerId);

    @Query("""
                SELECT COUNT(b) > 0 FROM Booking b 
                WHERE b.customer = :customer 
                AND b.status <> com.ljuslin.wigellMCRental.entity.BookingStatus.CANCELLED 
                AND b.endDate > :date
            """)
    boolean customerHasActiveFutureBookings(@Param("customer") Customer customer,
                                            @Param("date") LocalDate date);
    @Query("""
                SELECT COUNT(b) > 0 FROM Booking b 
                WHERE b.bike = :bike 
                AND b.status <> com.ljuslin.wigellMCRental.entity.BookingStatus.CANCELLED 
                AND b.endDate > :date
            """)
    boolean bikeHasActiveFutureBookings(@Param("bike") Bike bike, @Param("date") LocalDate date);

}
