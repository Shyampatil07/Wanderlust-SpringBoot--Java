package com.wanderlust.repository;

import com.wanderlust.entity.Booking;
import com.wanderlust.entity.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface BookingRepository
        extends JpaRepository<Booking, Long> {

    List<Booking> findByUser_Id(Long userId);

    List<Booking> findByPropertyIdAndStatusNotAndCheckInLessThanAndCheckOutGreaterThan(
            Long propertyId,
            BookingStatus status,
            LocalDate checkOut,
            LocalDate checkIn
    );
    
    boolean existsByUserIdAndPropertyIdAndStatus(
            Long userId,
            Long propertyId,
            BookingStatus status
    );
    
    
}