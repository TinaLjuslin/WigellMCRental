package com.ljuslin.wigellMCRental.repository;

import com.ljuslin.wigellMCRental.entity.Bike;
import com.ljuslin.wigellMCRental.entity.BikeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface BikeRepository extends JpaRepository<Bike,Long> {
    @Query("""
    SELECT b FROM Bike b 
    WHERE b.status = com.ljuslin.wigellMCRental.entity.BikeStatus.AVAILABLE
    AND b.id NOT IN (
        SELECT bo.bike.id FROM Booking bo 
        WHERE bo.status != com.ljuslin.wigellMCRental.entity.BookingStatus.CANCELLED
        AND bo.startDate <= :to 
        AND bo.endDate >= :from
    )
""")List<Bike> findAvailableBikes(@Param("from") LocalDate from, @Param("to") LocalDate to);

}