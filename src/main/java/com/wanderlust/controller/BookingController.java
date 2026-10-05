package com.wanderlust.controller;

import com.wanderlust.dto.ApiResponse;
import com.wanderlust.dto.BookingRequest;
import com.wanderlust.dto.BookingResponse;
import com.wanderlust.dto.GuestBookingResponse;
import com.wanderlust.service.BookingService;
import com.wanderlust.dto.HostBookingResponse;

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
    public ResponseEntity<ApiResponse<List<GuestBookingResponse>>> getMyBookings() {

        List<GuestBookingResponse> bookings =
                bookingService.getMyBookings();

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Bookings fetched successfully",
                        bookings
                )
        );
    }
    
    @GetMapping("/host")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<List<HostBookingResponse>>> getHostBookings() {

        List<HostBookingResponse> bookings =
                bookingService.getHostBookings();

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Host bookings fetched successfully",
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
    
    @PatchMapping("/{id}/host-cancel")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<BookingResponse>> hostCancelBooking(
            @PathVariable Long id) {

        BookingResponse response =
                bookingService.hostCancelBooking(id);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Booking cancelled successfully by host",
                        response
                )
        );
    }
    
    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<Void>> deleteBooking(
            @PathVariable Long id) {

        bookingService.deleteBooking(id);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Booking deleted successfully",
                        null
                )
        );
    }
    
}