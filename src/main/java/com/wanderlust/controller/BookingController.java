package com.wanderlust.controller;

import com.wanderlust.dto.ApiResponse;
import com.wanderlust.dto.BookingRequest;
import com.wanderlust.dto.BookingResponse;
import com.wanderlust.service.BookingService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "Bookings",
        description = "Hotel booking management APIs"
)
@RestController
@RequestMapping("/api/bookings")
@SecurityRequirement(name = "bearerAuth")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(
            BookingService bookingService) {

        this.bookingService = bookingService;
    }

    @PostMapping
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<BookingResponse>> createBooking(
            @Valid @RequestBody BookingRequest request) {

        BookingResponse response =
                bookingService.createBooking(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        new ApiResponse<>(
                                true,
                                "Booking created successfully",
                                response
                        )
                );
    }
    
    @GetMapping("/my")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<List<BookingResponse>>> getMyBookings() {

        List<BookingResponse> bookings =
                bookingService.getMyBookings();

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Bookings fetched successfully",
                        bookings
                )
        );
    }
    
    @GetMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<BookingResponse>> getBookingById(
            @PathVariable Long id) {

        BookingResponse booking =
                bookingService.getBookingById(id);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Booking fetched successfully",
                        booking
                )
        );
    }
    
    @PatchMapping("/{id}/cancel")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<BookingResponse>> cancelBooking(
            @PathVariable Long id) {

        BookingResponse response =
                bookingService.cancelBooking(id);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Booking cancelled successfully",
                        response
                )
        );
    }
    
}