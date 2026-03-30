package com.ljuslin.wigellMCRental.repository;

import com.ljuslin.wigellMCRental.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface BookingRepository extends JpaRepository<Booking,Long> {

        @Query("SELECT COUNT(b) > 0 FROM Booking b " +
                "WHERE b.bike.id = :bikeId " +
                "AND b.startDate < :endDate " +
                "AND b.endDate > :startDate")
        boolean existsOverlappingBooking(@Param("bikeId") Long bikeId,
                                         @Param("startDate") LocalDate startDate,
                                         @Param("endDate") LocalDate endDate);
    }
