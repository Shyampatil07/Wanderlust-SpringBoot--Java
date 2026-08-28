package com.wanderlust.dto;

import com.wanderlust.entity.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class BookingResponse {

    private Long id;

    private Long userId;

    private Long propertyId;

    private String propertyTitle;

    private LocalDate checkIn;

    private LocalDate checkOut;

    private Integer guests;

    private BigDecimal totalPrice;

    private BookingStatus status;
}