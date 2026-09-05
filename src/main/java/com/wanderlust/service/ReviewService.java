package com.wanderlust.service;

import com.wanderlust.dto.ReviewRequest;
import com.wanderlust.dto.ReviewResponse;
import com.wanderlust.entity.*;
import com.wanderlust.exception.ResourceNotFoundException;
import com.wanderlust.repository.BookingRepository;
import com.wanderlust.repository.PropertyRepository;
import com.wanderlust.repository.ReviewRepository;
import com.wanderlust.repository.UserRepository;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final PropertyRepository propertyRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    public ReviewService(
            ReviewRepository reviewRepository,
            PropertyRepository propertyRepository,
            BookingRepository bookingRepository,
            UserRepository userRepository) {

        this.reviewRepository = reviewRepository;
        this.propertyRepository = propertyRepository;
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
    }

    public ReviewResponse createReview(
            Long propertyId,
            ReviewRequest request) {

        User currentUser = getCurrentUser();

        Property property = propertyRepository
                .findById(propertyId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Property not found with id: "
                                        + propertyId
                        ));

        // Check whether user booked this property
        boolean hasBooking =
                bookingRepository
                        .existsByUserIdAndPropertyIdAndStatus(
                                currentUser.getId(),
                                propertyId,
                                BookingStatus.CONFIRMED
                        );

        if (!hasBooking) {

            throw new AccessDeniedException(
                    "You can review a property only after booking it"
            );
        }

        // Prevent duplicate review
        boolean alreadyReviewed =
                reviewRepository
                        .existsByUserIdAndPropertyId(
                                currentUser.getId(),
                                propertyId
                        );

        if (alreadyReviewed) {

            throw new IllegalArgumentException(
                    "You have already reviewed this property"
            );
        }

        Review review = new Review();

        review.setUser(currentUser);
        review.setProperty(property);
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        review.setCreatedAt(LocalDateTime.now());

        Review savedReview =
                reviewRepository.save(review);

        return convertToResponse(savedReview);
    }

    public List<ReviewResponse> getPropertyReviews(
            Long propertyId) {

        if (!propertyRepository.existsById(propertyId)) {

            throw new ResourceNotFoundException(
                    "Property not found with id: "
                            + propertyId
            );
        }

        return reviewRepository
                .findByPropertyId(propertyId)
                .stream()
                .map(this::convertToResponse)
                .toList();
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

    private ReviewResponse convertToResponse(
            Review review) {

        return new ReviewResponse(
                review.getId(),
                review.getUser().getId(),
                review.getUser().getName(),
                review.getProperty().getId(),
                review.getRating(),
                review.getComment(),
                review.getCreatedAt()
        );
    }
    
    public ReviewResponse updateReview(
            Long propertyId,
            Long reviewId,
            ReviewRequest request) {

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Review not found with id: "
                                        + reviewId
                        ));

        // Make sure review belongs to this property
        if (!review.getProperty().getId()
                .equals(propertyId)) {

            throw new ResourceNotFoundException(
                    "Review not found for this property"
            );
        }

        User currentUser = getCurrentUser();

        // ADMIN can update any review
        if (!isAdmin(currentUser)) {

            // USER can update only own review
            if (!review.getUser().getId()
                    .equals(currentUser.getId())) {

                throw new AccessDeniedException(
                        "You are not allowed to update this review"
                );
            }
        }

        review.setRating(request.getRating());
        review.setComment(request.getComment());

        Review updatedReview =
                reviewRepository.save(review);

        return convertToResponse(updatedReview);
    }
    
    private boolean isAdmin(User user) {

        return user.getRole() != null
                && user.getRole().name().equals("ADMIN");
    }
    
    public void deleteReview(
            Long propertyId,
            Long reviewId) {

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Review not found with id: "
                                        + reviewId
                        ));

        // Make sure review belongs to this property
        if (!review.getProperty().getId()
                .equals(propertyId)) {

            throw new ResourceNotFoundException(
                    "Review not found for this property"
            );
        }

        User currentUser = getCurrentUser();

        // ADMIN can delete any review
        if (!isAdmin(currentUser)) {

            // USER can delete only own review
            if (!review.getUser().getId()
                    .equals(currentUser.getId())) {

                throw new AccessDeniedException(
                        "You are not allowed to delete this review"
                );
            }
        }

        reviewRepository.delete(review);
    }
    
}