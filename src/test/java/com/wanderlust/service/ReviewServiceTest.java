package com.wanderlust.service;

import com.wanderlust.dto.ReviewRequest;
import com.wanderlust.dto.ReviewResponse;
import com.wanderlust.entity.Booking;
import com.wanderlust.entity.BookingStatus;
import com.wanderlust.entity.Property;
import com.wanderlust.entity.Review;
import com.wanderlust.entity.Role;
import com.wanderlust.entity.User;
import com.wanderlust.exception.ResourceNotFoundException;
import com.wanderlust.repository.BookingRepository;
import com.wanderlust.repository.PropertyRepository;
import com.wanderlust.repository.ReviewRepository;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

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
    private ReviewService reviewService;
    
//   @Test
//    void shouldRejectReviewWhenUserHasNotBookedProperty() {
//
//        // ---------- Arrange ----------
//
//        User user = new User();
//
//        user.setId(1L);
//        user.setEmail("ram@gmail.com");
//        user.setRole(Role.USER);
//
//
//        Property property = new Property();
//
//        property.setId(10L);
//        property.setTitle("Pune Villa");
//
//
//        // ---------- Authentication ----------
//
//        when(authentication.getName())
//                .thenReturn("ram@gmail.com");
//
//        when(securityContext.getAuthentication())
//                .thenReturn(authentication);
//
//        SecurityContextHolder.setContext(
//                securityContext
//        );
//
//
//        // ---------- Current user ----------
//
//        when(userRepository.findByEmail("ram@gmail.com"))
//                .thenReturn(
//                        Optional.of(user)
//                );
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
//        // ---------- User has NOT booked property ----------
//
//        when(
//            bookingRepository.existsByUserIdAndPropertyIdAndStatus(
//                1L,
//                10L,
//                BookingStatus.CONFIRMED
//            )
//        ).thenReturn(false);
//
//
//        // ---------- Review Request ----------
//
//        ReviewRequest request = new ReviewRequest();
//
//        request.setRating(5);
//        request.setComment(
//                "Excellent property!"
//        );
//
//
//        // ---------- Act + Assert ----------
//
//        assertThrows(
//                AccessDeniedException.class,
//                () -> reviewService.createReview(
//                        10L,
//                        request
//                )
//        );
//
//
//        // ---------- Verify ----------
//
//        verify(propertyRepository)
//                .findById(10L);
//
//        verify(bookingRepository)
//                .existsByUserIdAndPropertyIdAndStatus(
//                        1L,
//                        10L,
//                        BookingStatus.CONFIRMED
//                );
//
//        verify(reviewRepository, never())
//                .save(any(Review.class));
//    }
    
    @Test
    void shouldRejectDuplicateReview() {

        // ---------- Arrange ----------

        User user = new User();

        user.setId(1L);
        user.setEmail("ram@gmail.com");
        user.setRole(Role.USER);


        Property property = new Property();

        property.setId(10L);
        property.setTitle("Pune Villa");


        // ---------- Authentication ----------

        when(authentication.getName())
                .thenReturn("ram@gmail.com");

        when(securityContext.getAuthentication())
                .thenReturn(authentication);

        SecurityContextHolder.setContext(
                securityContext
        );


        // ---------- Current user ----------

        when(userRepository.findByEmail("ram@gmail.com"))
                .thenReturn(
                        Optional.of(user)
                );


        // ---------- Property ----------

        when(propertyRepository.findById(10L))
                .thenReturn(
                        Optional.of(property)
                );


        // ---------- User has booked property ----------

        when(
            bookingRepository.existsByUserIdAndPropertyIdAndStatus(
                1L,
                10L,
                BookingStatus.CONFIRMED
            )
        ).thenReturn(true);


        // ---------- User has already reviewed property ----------

        when(
            reviewRepository.existsByUserIdAndPropertyId(
                1L,
                10L
            )
        ).thenReturn(true);


        // ---------- Review Request ----------

        ReviewRequest request = new ReviewRequest();

        request.setRating(5);
        request.setComment(
                "Excellent property!"
        );


        // ---------- Act + Assert ----------

        assertThrows(
                IllegalArgumentException.class,
                () -> reviewService.createReview(
                        10L,
                        request
                )
        );


        // ---------- Verify ----------

        verify(bookingRepository)
                .existsByUserIdAndPropertyIdAndStatus(
                        1L,
                        10L,
                        BookingStatus.CONFIRMED
                );

        verify(reviewRepository)
                .existsByUserIdAndPropertyId(
                        1L,
                        10L
                );

        // Review must NOT be saved
        verify(reviewRepository, never())
                .save(any(Review.class));
    }
    
    
    @Test
    void shouldThrowExceptionWhenPropertyNotFound() {

        // ---------- Arrange ----------

        User user = new User();

        user.setId(1L);
        user.setEmail("ram@gmail.com");
        user.setRole(Role.USER);


        // ---------- Authentication ----------

        when(authentication.getName())
                .thenReturn("ram@gmail.com");

        when(securityContext.getAuthentication())
                .thenReturn(authentication);

        SecurityContextHolder.setContext(
                securityContext
        );


        // ---------- Current user ----------

        when(userRepository.findByEmail("ram@gmail.com"))
                .thenReturn(
                        Optional.of(user)
                );


        // ---------- Property NOT found ----------

        when(propertyRepository.findById(10L))
                .thenReturn(
                        Optional.empty()
                );


        // ---------- Review Request ----------

        ReviewRequest request = new ReviewRequest();

        request.setRating(5);
        request.setComment(
                "Excellent property!"
        );


        // ---------- Act + Assert ----------

        assertThrows(
                ResourceNotFoundException.class,
                () -> reviewService.createReview(
                        10L,
                        request
                )
        );


        // ---------- Verify ----------

        verify(propertyRepository)
                .findById(10L);

        // Booking check should never happen
        verify(
                bookingRepository,
                never()
        ).existsByUserIdAndPropertyIdAndStatus(
                anyLong(),
                anyLong(),
                any(BookingStatus.class)
        );

        // Review should never be saved
        verify(
                reviewRepository,
                never()
        ).save(any(Review.class));
    }
    
    @Test
    void shouldCreateReviewSuccessfully() {

        // ---------- Arrange ----------

        User user = new User();
        user.setId(1L);
        user.setName("Ram");
        user.setEmail("ram@gmail.com");
        user.setRole(Role.USER);

        Property property = new Property();
        property.setId(10L);
        property.setTitle("Pune Villa");

        // Authentication
        when(authentication.getName()).thenReturn("ram@gmail.com");
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByEmail("ram@gmail.com"))
                .thenReturn(Optional.of(user));

        when(propertyRepository.findById(10L))
                .thenReturn(Optional.of(property));

        // User has confirmed booking
        when(bookingRepository.existsByUserIdAndPropertyIdAndStatus(
                1L,
                10L,
                BookingStatus.CONFIRMED
        )).thenReturn(true);

        // No duplicate review
        when(reviewRepository.existsByUserIdAndPropertyId(
                1L,
                10L
        )).thenReturn(false);

        ReviewRequest request = new ReviewRequest();
        request.setRating(5);
        request.setComment("Excellent Stay!");

        Review savedReview = new Review();
        savedReview.setId(101L);
        savedReview.setRating(5);
        savedReview.setComment("Excellent Stay!");
        savedReview.setUser(user);
        savedReview.setProperty(property);
        savedReview.setCreatedAt(LocalDateTime.now());

        when(reviewRepository.save(any(Review.class)))
                .thenReturn(savedReview);

        // ---------- Act ----------

        ReviewResponse response =
                reviewService.createReview(10L, request);

        // ---------- Assert ----------

        assertNotNull(response);
        assertEquals(101L, response.getId());
        assertEquals(5, response.getRating());
        assertEquals("Excellent Stay!", response.getComment());
        assertEquals("Ram", response.getUserName());

        verify(reviewRepository).save(any(Review.class));
    }
    
    @Test
    void shouldGetReviewsForProperty() {

        Property property = new Property();
        property.setId(10L);

        User user = new User();
        user.setId(1L);
        user.setName("Ram");

        Review review = new Review();
        review.setId(1L);
        review.setRating(5);
        review.setComment("Excellent!");
        review.setUser(user);
        review.setProperty(property);
        review.setCreatedAt(LocalDateTime.now());

        when(propertyRepository.existsById(10L))
                .thenReturn(true);

        when(reviewRepository.findByPropertyId(10L))
                .thenReturn(List.of(review));

        List<ReviewResponse> result =
                reviewService.getPropertyReviews(10L);

        assertEquals(1, result.size());
        assertEquals(5, result.get(0).getRating());
        assertEquals("Excellent!", result.get(0).getComment());

        verify(reviewRepository).findByPropertyId(10L);
    }
    
    @Test
    void shouldAllowOwnerToUpdateReview() {

        User owner = new User();
        owner.setId(1L);
        owner.setEmail("ram@gmail.com");
        owner.setRole(Role.USER);

        Property property = new Property();
        property.setId(10L);

        Review review = new Review();
        review.setId(20L);
        review.setUser(owner);
        review.setProperty(property);
        review.setRating(4);
        review.setComment("Good");

        when(authentication.getName()).thenReturn("ram@gmail.com");
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByEmail("ram@gmail.com"))
                .thenReturn(Optional.of(owner));

        when(reviewRepository.findById(20L))
                .thenReturn(Optional.of(review));

        ReviewRequest request = new ReviewRequest();
        request.setRating(5);
        request.setComment("Excellent");

        when(reviewRepository.save(any(Review.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ReviewResponse response =
                reviewService.updateReview(10L, 20L, request);

        assertEquals(5, response.getRating());
        assertEquals("Excellent", response.getComment());

        verify(reviewRepository).save(review);
    }
    
    @Test
    void shouldNotAllowUserToUpdateAnotherUsersReview() {

        User owner = new User();
        owner.setId(1L);
        owner.setRole(Role.USER);

        User otherUser = new User();
        otherUser.setId(2L);
        otherUser.setEmail("user@gmail.com");
        otherUser.setRole(Role.USER);

        Property property = new Property();
        property.setId(10L);

        Review review = new Review();
        review.setId(20L);
        review.setUser(owner);
        review.setProperty(property);

        when(authentication.getName()).thenReturn("user@gmail.com");
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByEmail("user@gmail.com"))
                .thenReturn(Optional.of(otherUser));

        when(reviewRepository.findById(20L))
                .thenReturn(Optional.of(review));

        ReviewRequest request = new ReviewRequest();
        request.setRating(1);
        request.setComment("Bad");

        assertThrows(
                AccessDeniedException.class,
                () -> reviewService.updateReview(10L, 20L, request)
        );

        verify(reviewRepository, never()).save(any());
    }
    
    @Test
    void shouldAllowAdminToUpdateAnyReview() {

        User owner = new User();
        owner.setId(1L);
        owner.setRole(Role.USER);

        User admin = new User();
        admin.setId(99L);
        admin.setEmail("admin@gmail.com");
        admin.setRole(Role.ADMIN);

        Property property = new Property();
        property.setId(10L);

        Review review = new Review();
        review.setId(20L);
        review.setUser(owner);
        review.setProperty(property);

        when(authentication.getName()).thenReturn("admin@gmail.com");
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByEmail("admin@gmail.com"))
                .thenReturn(Optional.of(admin));

        when(reviewRepository.findById(20L))
                .thenReturn(Optional.of(review));

        ReviewRequest request = new ReviewRequest();
        request.setRating(5);
        request.setComment("Updated by Admin");

        when(reviewRepository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ReviewResponse response =
                reviewService.updateReview(10L, 20L, request);

        assertEquals(5, response.getRating());
        assertEquals("Updated by Admin", response.getComment());

        verify(reviewRepository).save(review);
    }

    @AfterEach
    void cleanup() {
        SecurityContextHolder.clearContext();
    }
}