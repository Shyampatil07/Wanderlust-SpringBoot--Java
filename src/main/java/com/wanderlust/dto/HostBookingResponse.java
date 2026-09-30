package com.wanderlust.dto;

import com.wanderlust.entity.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class HostBookingResponse {

    private Long id;

    private Long guestId;
    private String guestName;
    private String guestEmail;

    private Long propertyId;
    private String propertyTitle;
    private String propertyImageUrl;

    private LocalDate checkIn;
    private LocalDate checkOut;

    private Integer guests;

    private BigDecimal totalPrice;

    private BookingStatus status;
}