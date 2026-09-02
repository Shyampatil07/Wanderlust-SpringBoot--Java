package com.wanderlust.service;

import com.wanderlust.dto.BookingRequest;
import com.wanderlust.dto.BookingResponse;
import com.wanderlust.entity.Booking;
import com.wanderlust.entity.BookingStatus;
import com.wanderlust.entity.Property;
import com.wanderlust.entity.Role;
import com.wanderlust.entity.User;
import com.wanderlust.exception.ResourceNotFoundException;
import com.wanderlust.repository.BookingRepository;
import com.wanderlust.repository.PropertyRepository;
import com.wanderlust.repository.UserRepository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;


import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private PropertyRepository propertyRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private BookingService bookingService;
    
//    @Test
//    void shouldCreateBookingSuccessfully() {
//
//        // ---------- Arrange ----------
//
//        User user = new User();
//
//        user.setId(1L);
//        user.setName("Shyam");
//        user.setEmail("shyam@gmail.com");
//
//
//        Property property = new Property();
//
//        property.setId(10L);
//        property.setTitle("Pune Villa");
//        property.setPricePerNight(5000.0);
//        property.setMaxGuests(4);
//
//
//        BookingRequest request = new BookingRequest();
//
//        request.setPropertyId(10L);
//        request.setCheckIn(
//                LocalDate.of(2026, 9, 10)
//        );
//        request.setCheckOut(
//                LocalDate.of(2026, 9, 13)
//        );
//        request.setGuests(2);
//
//
//        // ---------- Mock authenticated user ----------
//
//        when(authentication.getName())
//                .thenReturn("shyam@gmail.com");
//
//        when(securityContext.getAuthentication())
//                .thenReturn(authentication);
//
//        SecurityContextHolder.setContext(
//                securityContext
//        );
//
//
//        // ---------- Mock User ----------
//
//        when(userRepository.findByEmail(
//                "shyam@gmail.com"
//        )).thenReturn(
//                Optional.of(user)
//        );
//
//
//        // ---------- Mock Property ----------
//
//        when(propertyRepository.findById(10L))
//                .thenReturn(
//                        Optional.of(property)
//                );
//
//
//        // ---------- No overlapping booking ----------
//
//        when(
//            bookingRepository
//                .findByPropertyIdAndStatusNotAndCheckInLessThanAndCheckOutGreaterThan(
//                        eq(10L),
//                        eq(BookingStatus.CANCELLED),
//                        any(LocalDate.class),
//                        any(LocalDate.class)
//                )
//        ).thenReturn(
//                java.util.List.of()
//        );
//
//
//        // ---------- Saved Booking ----------
//
//        Booking savedBooking = new Booking();
//
//        savedBooking.setId(100L);
//        savedBooking.setUser(user);
//        savedBooking.setProperty(property);
//        savedBooking.setCheckIn(
//                request.getCheckIn()
//        );
//        savedBooking.setCheckOut(
//                request.getCheckOut()
//        );
//        savedBooking.setGuests(
//                request.getGuests()
//        );
//
//
//        when(bookingRepository.save(
//                any(Booking.class)
//        )).thenReturn(
//                savedBooking
//        );
//
//
//        // ---------- Act ----------
//
//        BookingResponse response =
//                bookingService.createBooking(request);
//
//
//        // ---------- Assert ----------
//
//        assertNotNull(response);
//
//        assertEquals(
//                100L,
//                response.getId()
//        );
//
//
//        // ---------- Verify ----------
//
//        verify(userRepository)
//                .findByEmail("shyam@gmail.com");
//
//        verify(propertyRepository)
//                .findById(10L);
//
//        verify(bookingRepository)
//                .save(any(Booking.class));
//    }
//    
//    
//    @Test
//    void shouldThrowExceptionWhenPropertyNotFound() {
//
//        // Arrange
//
//        User user = new User();
//        user.setId(1L);
//        user.setEmail("shyam@gmail.com");
//
//        BookingRequest request = new BookingRequest();
//
//        request.setPropertyId(99L);
//        request.setCheckIn(
//                LocalDate.of(2026, 9, 10)
//        );
//        request.setCheckOut(
//                LocalDate.of(2026, 9, 13)
//        );
//        request.setGuests(2);
//
//
//        // Mock authenticated user
//
//        when(authentication.getName())
//                .thenReturn("shyam@gmail.com");
//
//        when(securityContext.getAuthentication())
//                .thenReturn(authentication);
//
//        SecurityContextHolder.setContext(
//                securityContext
//        );
//
//
//        when(userRepository.findByEmail(
//                "shyam@gmail.com"
//        )).thenReturn(
//                Optional.of(user)
//        );
//
//
//        // Property doesn't exist
//
//        when(propertyRepository.findById(99L))
//                .thenReturn(Optional.empty());
//
//
//        // Act + Assert
//
//        assertThrows(
//                ResourceNotFoundException.class,
//                () -> bookingService.createBooking(request)
//        );
//
//
//        // Verify
//
//        verify(propertyRepository)
//                .findById(99L);
//
//        verify(bookingRepository, never())
//                .save(any(Booking.class));
//    }
//    
//    @Test
//    void shouldRejectBookingWhenGuestsExceedPropertyCapacity() {
//
//        // Arrange
//
//        User user = new User();
//        user.setId(1L);
//        user.setEmail("shyam@gmail.com");
//
//
//        Property property = new Property();
//        property.setId(10L);
//        property.setMaxGuests(4);
//        property.setPricePerNight(5000.0);
//
//
//        BookingRequest request = new BookingRequest();
//
//        request.setPropertyId(10L);
//
//        request.setCheckIn(
//                LocalDate.of(2026, 9, 10)
//        );
//
//        request.setCheckOut(
//                LocalDate.of(2026, 9, 13)
//        );
//
//        request.setGuests(6);
//
//
//        // Authentication
//
//        when(authentication.getName())
//                .thenReturn("shyam@gmail.com");
//
//        when(securityContext.getAuthentication())
//                .thenReturn(authentication);
//
//        SecurityContextHolder.setContext(
//                securityContext
//        );
//
//
//        when(userRepository.findByEmail(
//                "shyam@gmail.com"
//        )).thenReturn(
//                Optional.of(user)
//        );
//
//
//        when(propertyRepository.findById(10L))
//                .thenReturn(
//                        Optional.of(property)
//                );
//
//
//        // Act + Assert
//
//        assertThrows(
//                IllegalArgumentException.class,
//                () -> bookingService.createBooking(request)
//        );
//
//
//        // Booking must not be saved
//
//        verify(bookingRepository, never())
//                .save(any(Booking.class));
//    }
//    
//    @Test
//    void shouldRejectOverlappingBooking() {
//
//        // ---------- Arrange ----------
//
//        User user = new User();
//        user.setId(1L);
//        user.setEmail("shyam@gmail.com");
//
//
//        Property property = new Property();
//        property.setId(10L);
//        property.setPricePerNight(5000.0);
//        property.setMaxGuests(4);
//
//
//        BookingRequest request = new BookingRequest();
//
//        request.setPropertyId(10L);
//
//        request.setCheckIn(
//                LocalDate.of(2026, 9, 12)
//        );
//
//        request.setCheckOut(
//                LocalDate.of(2026, 9, 17)
//        );
//
//        request.setGuests(2);
//
//
//        // ---------- Authentication ----------
//
//        when(authentication.getName())
//                .thenReturn("shyam@gmail.com");
//
//        when(securityContext.getAuthentication())
//                .thenReturn(authentication);
//
//        SecurityContextHolder.setContext(
//                securityContext
//        );
//
//
//        // ---------- User ----------
//
//        when(userRepository.findByEmail(
//                "shyam@gmail.com"
//        )).thenReturn(
//                Optional.of(user)
//        );
//
//
//        // ---------- Property ----------
//
//        when(propertyRepository.findById(10L))
//                .thenReturn(
//                        Optional.of(property)
//                );
//
//
//        // ---------- Existing booking ----------
//
//        Booking existingBooking = new Booking();
//
//        existingBooking.setId(50L);
//        existingBooking.setUser(user);
//        existingBooking.setProperty(property);
//
//        existingBooking.setCheckIn(
//                LocalDate.of(2026, 9, 10)
//        );
//
//        existingBooking.setCheckOut(
//                LocalDate.of(2026, 9, 15)
//        );
//
//
//        // ---------- Repository finds overlap ----------
//
//        when(
//            bookingRepository
//                .findByPropertyIdAndStatusNotAndCheckInLessThanAndCheckOutGreaterThan(
//                    eq(10L),
//                    eq(BookingStatus.CANCELLED),
//                    any(LocalDate.class),
//                    any(LocalDate.class)
//                )
//        ).thenReturn(
//                List.of(existingBooking)
//        );
//
//
//        // ---------- Act + Assert ----------
//
//        assertThrows(
//                IllegalArgumentException.class,
//                () -> bookingService.createBooking(request)
//        );
//
//
//        // ---------- Verify ----------
//
//        verify(bookingRepository, never())
//                .save(any(Booking.class));
//    }
//    
//    @Test
//    void shouldGetMyBookings() {
//
//        // ---------- Arrange ----------
//
//        User user = new User();
//        user.setId(1L);
//        user.setEmail("shyam@gmail.com");
//
//
//        Property property = new Property();
//        property.setId(10L);
//        property.setTitle("Pune Villa");
//
//
//        Booking booking = new Booking();
//        booking.setId(100L);
//        booking.setUser(user);
//        booking.setProperty(property);
//
//        booking.setCheckIn(
//                LocalDate.of(2026, 9, 10)
//        );
//
//        booking.setCheckOut(
//                LocalDate.of(2026, 9, 13)
//        );
//
//        booking.setGuests(2);
//
//
//        // ---------- Authentication ----------
//
//        when(authentication.getName())
//                .thenReturn("shyam@gmail.com");
//
//        when(securityContext.getAuthentication())
//                .thenReturn(authentication);
//
//        SecurityContextHolder.setContext(
//                securityContext
//        );
//
//
//        // ---------- User lookup ----------
//
//        when(userRepository.findByEmail(
//                "shyam@gmail.com"
//        )).thenReturn(
//                Optional.of(user)
//        );
//
//
//        // ---------- User's bookings ----------
//
//        when(bookingRepository.findByUser_Id(1L))
//                .thenReturn(
//                        List.of(booking)
//                );
//
//
//        // ---------- Act ----------
//
//        List<BookingResponse> result =
//                bookingService.getMyBookings();
//
//
//        // ---------- Assert ----------
//
//        assertNotNull(result);
//
//        assertEquals(
//                1,
//                result.size()
//        );
//
//
//        assertEquals(
//                100L,
//                result.get(0).getId()
//        );
//
//
//        // ---------- Verify ----------
//
//        verify(userRepository)
//                .findByEmail("shyam@gmail.com");
//
//        verify(bookingRepository)
//                .findByUser_Id(1L);
//    }
//    
//    @Test
//    void shouldNotAllowUserToViewAnotherUsersBooking() {
//
//        // ---------- Arrange ----------
//
//        User owner = new User();
//
//        owner.setId(1L);
//        owner.setEmail("owner@gmail.com");
//        owner.setRole(Role.USER);
//
//
//        User currentUser = new User();
//
//        currentUser.setId(2L);
//        currentUser.setEmail("user@gmail.com");
//        currentUser.setRole(Role.USER);
//
//
//        Property property = new Property();
//
//        property.setId(10L);
//
//
//        Booking booking = new Booking();
//
//        booking.setId(100L);
//        booking.setUser(owner);
//        booking.setProperty(property);
//
//
//        when(bookingRepository.findById(100L))
//                .thenReturn(
//                        Optional.of(booking)
//                );
//
//
//        // ---------- Logged-in user = User B ----------
//
//        when(authentication.getName())
//                .thenReturn("user@gmail.com");
//
//        when(securityContext.getAuthentication())
//                .thenReturn(authentication);
//
//        SecurityContextHolder.setContext(
//                securityContext
//        );
//
//
//        when(userRepository.findByEmail(
//                "user@gmail.com"
//        )).thenReturn(
//                Optional.of(currentUser)
//        );
//
//
//        // ---------- Act + Assert ----------
//
//        assertThrows(
//                AccessDeniedException.class,
//                () -> bookingService.getBookingById(100L)
//        );
//
//
//        // ---------- Verify ----------
//
//        verify(bookingRepository)
//                .findById(100L);
//    }
    
//    @Test
//    void shouldAllowAdminToViewAnyBooking() {
//
//        // ---------- Arrange ----------
//
//        User owner = new User();
//
//        owner.setId(1L);
//        owner.setEmail("owner@gmail.com");
//        owner.setRole(Role.USER);
//
//
//        User admin = new User();
//
//        admin.setId(99L);
//        admin.setEmail("admin@gmail.com");
//        admin.setRole(Role.ADMIN);
//
//
//        Property property = new Property();
//
//        property.setId(10L);
//        property.setTitle("Pune Villa");
//
//
//        Booking booking = new Booking();
//
//        booking.setId(100L);
//        booking.setUser(owner);
//        booking.setProperty(property);
//
//        booking.setCheckIn(
//                LocalDate.of(2026, 9, 10)
//        );
//
//        booking.setCheckOut(
//                LocalDate.of(2026, 9, 13)
//        );
//
//        booking.setGuests(2);
//
//
//        // ---------- Booking ----------
//
//        when(bookingRepository.findById(100L))
//                .thenReturn(
//                        Optional.of(booking)
//                );
//
//
//        // ---------- Logged-in user = ADMIN ----------
//
//        when(authentication.getName())
//                .thenReturn("admin@gmail.com");
//
//        when(securityContext.getAuthentication())
//                .thenReturn(authentication);
//
//        SecurityContextHolder.setContext(
//                securityContext
//        );
//
//
//        when(userRepository.findByEmail(
//                "admin@gmail.com"
//        )).thenReturn(
//                Optional.of(admin)
//        );
//
//
//        // ---------- Act ----------
//
//        BookingResponse response =
//                bookingService.getBookingById(100L);
//
//
//        // ---------- Assert ----------
//
//        assertNotNull(response);
//
//        assertEquals(
//                100L,
//                response.getId()
//        );
//
//
//        // ---------- Verify ----------
//
//        verify(bookingRepository)
//                .findById(100L);
//    }
    
//    @Test
//    void shouldAllowUserToCancelOwnBooking() {
//
//        // ---------- Arrange ----------
//
//        User user = new User();
//
//        user.setId(1L);
//        user.setEmail("shyam@gmail.com");
//        user.setRole(Role.USER);
//
//
//        Property property = new Property();
//
//        property.setId(10L);
//
//
//        Booking booking = new Booking();
//
//        booking.setId(100L);
//        booking.setUser(user);
//        booking.setProperty(property);
//        booking.setStatus(BookingStatus.CONFIRMED);
//
//
//        when(bookingRepository.findById(100L))
//                .thenReturn(
//                        Optional.of(booking)
//                );
//
//
//        // ---------- Authentication ----------
//
//        when(authentication.getName())
//                .thenReturn("shyam@gmail.com");
//
//        when(securityContext.getAuthentication())
//                .thenReturn(authentication);
//
//        SecurityContextHolder.setContext(
//                securityContext
//        );
//
//
//        when(userRepository.findByEmail(
//                "shyam@gmail.com"
//        )).thenReturn(
//                Optional.of(user)
//        );
//
//
//        // ---------- Save cancelled booking ----------
//
//        when(bookingRepository.save(
//                any(Booking.class)
//        )).thenAnswer(
//                invocation ->
//                        invocation.getArgument(0)
//        );
//
//
//        // ---------- Act ----------
//
//        BookingResponse response =
//                bookingService.cancelBooking(100L);
//
//
//        // ---------- Assert ----------
//
//        assertNotNull(response);
//
//        assertEquals(
//                BookingStatus.CANCELLED,
//                booking.getStatus()
//        );
//
//
//        // ---------- Verify ----------
//
//        verify(bookingRepository)
//                .save(booking);
//    }
    
    @Test
    void shouldNotAllowUserToCancelAnotherUsersBooking() {

        // ---------- Arrange ----------

        User owner = new User();

        owner.setId(1L);
        owner.setEmail("owner@gmail.com");
        owner.setRole(Role.USER);


        User currentUser = new User();

        currentUser.setId(2L);
        currentUser.setEmail("user@gmail.com");
        currentUser.setRole(Role.USER);


        Property property = new Property();

        property.setId(10L);


        Booking booking = new Booking();

        booking.setId(100L);
        booking.setUser(owner);
        booking.setProperty(property);
        booking.setStatus(BookingStatus.CONFIRMED);


        when(bookingRepository.findById(100L))
                .thenReturn(
                        Optional.of(booking)
                );


        // ---------- Logged-in user = User B ----------

        when(authentication.getName())
                .thenReturn("user@gmail.com");

        when(securityContext.getAuthentication())
                .thenReturn(authentication);

        SecurityContextHolder.setContext(
                securityContext
        );


        when(userRepository.findByEmail(
                "user@gmail.com"
        )).thenReturn(
                Optional.of(currentUser)
        );


        // ---------- Act + Assert ----------

        assertThrows(
                AccessDeniedException.class,
                () -> bookingService.cancelBooking(100L)
        );


        // Booking must NOT be changed/saved

        assertEquals(
                BookingStatus.CONFIRMED,
                booking.getStatus()
        );

        verify(bookingRepository, never())
                .save(any(Booking.class));
    }
    
    @Test
    void shouldNotCancelAlreadyCancelledBooking() {

        // ---------- Arrange ----------

        User user = new User();

        user.setId(1L);
        user.setEmail("shyam@gmail.com");
        user.setRole(Role.USER);


        Property property = new Property();

        property.setId(10L);


        Booking booking = new Booking();

        booking.setId(100L);
        booking.setUser(user);
        booking.setProperty(property);

        // Already cancelled
        booking.setStatus(BookingStatus.CANCELLED);


        when(bookingRepository.findById(100L))
                .thenReturn(Optional.of(booking));


        // ---------- Authentication ----------

        when(authentication.getName())
                .thenReturn("shyam@gmail.com");

        when(securityContext.getAuthentication())
                .thenReturn(authentication);

        SecurityContextHolder.setContext(
                securityContext
        );


        when(userRepository.findByEmail(
                "shyam@gmail.com"
        )).thenReturn(Optional.of(user));


        // ---------- Act + Assert ----------

        assertThrows(
                IllegalArgumentException.class,
                () -> bookingService.cancelBooking(100L)
        );


        // ---------- Verify ----------

        verify(bookingRepository, never())
                .save(any(Booking.class));
    }
    
    


    @AfterEach
    void cleanup() {
        SecurityContextHolder.clearContext();
    }
}