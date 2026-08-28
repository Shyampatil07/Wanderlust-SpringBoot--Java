package com.wanderlust.service;

import com.wanderlust.dto.BookingRequest;
import com.wanderlust.dto.BookingResponse;
import com.wanderlust.entity.Booking;
import com.wanderlust.entity.BookingStatus;
import com.wanderlust.entity.Property;
import com.wanderlust.entity.User;
import com.wanderlust.exception.ResourceNotFoundException;
import com.wanderlust.repository.BookingRepository;
import com.wanderlust.repository.PropertyRepository;
import com.wanderlust.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;

    public BookingService(
            BookingRepository bookingRepository,
            PropertyRepository propertyRepository,
            UserRepository userRepository) {

        this.bookingRepository = bookingRepository;
        this.propertyRepository = propertyRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public BookingResponse createBooking(BookingRequest request) {

        User currentUser = getCurrentUser();

        Property property = propertyRepository.findById(
                request.getPropertyId()
        ).orElseThrow(() ->
                new ResourceNotFoundException(
                        "Property not found with id: "
                                + request.getPropertyId()
                )
        );

        validateDates(
                request.getCheckIn(),
                request.getCheckOut()
        );

        if (request.getGuests() > property.getMaxGuests()) {

            throw new IllegalArgumentException(
                    "Number of guests exceeds property capacity"
            );
        }

        List<Booking> overlappingBookings =
                bookingRepository
                        .findByPropertyIdAndStatusNotAndCheckInLessThanAndCheckOutGreaterThan(
                                property.getId(),
                                BookingStatus.CANCELLED,
                                request.getCheckOut(),
                                request.getCheckIn()
                        );

        if (!overlappingBookings.isEmpty()) {

            throw new IllegalArgumentException(
                    "Property is not available for the selected dates"
            );
        }

        long numberOfNights =
                ChronoUnit.DAYS.between(
                        request.getCheckIn(),
                        request.getCheckOut()
                );

        BigDecimal pricePerNight =
                BigDecimal.valueOf(
                        property.getPricePerNight()
                );

        BigDecimal totalPrice =
                pricePerNight.multiply(
                        BigDecimal.valueOf(numberOfNights)
                );

        Booking booking = new Booking();

        booking.setUser(currentUser);
        booking.setProperty(property);
        booking.setCheckIn(request.getCheckIn());
        booking.setCheckOut(request.getCheckOut());
        booking.setGuests(request.getGuests());
        booking.setTotalPrice(totalPrice);
        booking.setStatus(BookingStatus.CONFIRMED);

        Booking savedBooking =
                bookingRepository.save(booking);

        return convertToResponse(savedBooking);
    }

    private void validateDates(
            LocalDate checkIn,
            LocalDate checkOut) {

        if (!checkOut.isAfter(checkIn)) {

            throw new IllegalArgumentException(
                    "Check-out date must be after check-in date"
            );
        }
    }

    private User getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Authenticated user not found"
                        ));
    }

    private BookingResponse convertToResponse(
            Booking booking) {

        return new BookingResponse(
                booking.getId(),
                booking.getUser().getId(),
                booking.getProperty().getId(),
                booking.getProperty().getTitle(),
                booking.getCheckIn(),
                booking.getCheckOut(),
                booking.getGuests(),
                booking.getTotalPrice(),
                booking.getStatus()
        );
    }
    
    public List<BookingResponse> getMyBookings() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"
                        ));

        return bookingRepository
                .findByUser_Id(user.getId())
                .stream()
                .map(this::convertToResponse)
                .toList();
    }
    
    public BookingResponse getBookingById(Long id) {

        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Booking not found with id: " + id
                        ));

        User currentUser = getCurrentUser();

        // ADMIN can view any booking
        if (isAdmin(currentUser)) {
            return convertToResponse(booking);
        }

        // USER can view only their own booking
        if (!booking.getUser().getId()
                .equals(currentUser.getId())) {

            throw new AccessDeniedException(
                    "You are not allowed to view this booking"
            );
        }

        return convertToResponse(booking);
    }
    
    private boolean isAdmin(User user) {

        return user.getRole() != null
                && user.getRole().name().equals("ADMIN");
    }
    
    public BookingResponse cancelBooking(Long id) {

        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Booking not found with id: " + id
                        ));

        User currentUser = getCurrentUser();

        // USER can cancel only own booking
        if (!isAdmin(currentUser)) {

            if (!booking.getUser().getId()
                    .equals(currentUser.getId())) {

                throw new AccessDeniedException(
                        "You are not allowed to cancel this booking"
                );
            }
        }

        // Prevent cancelling an already cancelled booking
        if (booking.getStatus() == BookingStatus.CANCELLED) {

            throw new IllegalArgumentException(
                    "Booking is already cancelled"
            );
        }

        booking.setStatus(BookingStatus.CANCELLED);

        Booking cancelledBooking =
                bookingRepository.save(booking);

        return convertToResponse(cancelledBooking);
    }
    
}