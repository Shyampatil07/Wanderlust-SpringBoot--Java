package com.wanderlust.service;

import com.wanderlust.dto.HostProfileResponse;
import com.wanderlust.dto.HostProfileUpdateRequest;
import com.wanderlust.dto.HostReviewResponse;
import com.wanderlust.dto.PropertyResponse;
import com.wanderlust.entity.Review;
import com.wanderlust.entity.User;
import com.wanderlust.exception.ResourceNotFoundException;
import com.wanderlust.repository.PropertyRepository;
import com.wanderlust.repository.ReviewRepository;
import com.wanderlust.repository.UserRepository;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class HostProfileService {

    private final UserRepository userRepository;
    private final PropertyRepository propertyRepository;
    private final ReviewRepository reviewRepository;

    public HostProfileService(
            UserRepository userRepository,
            PropertyRepository propertyRepository,
            ReviewRepository reviewRepository,
            PropertyService propertyService,
            CloudinaryService cloudinaryService) {

        this.userRepository = userRepository;
        this.propertyRepository = propertyRepository;
        this.reviewRepository = reviewRepository;
        this.propertyService = propertyService;
        this.cloudinaryService = cloudinaryService;
    }
    
    public HostProfileResponse uploadProfilePhoto(
            MultipartFile file) throws IOException {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException(
                    "Profile image is required"
            );
        }

        if (file.getContentType() == null ||
                !file.getContentType().startsWith("image/")) {

            throw new IllegalArgumentException(
                    "Only image files are allowed"
            );
        }

        User host = getCurrentUser();

        String imageUrl =
                cloudinaryService.uploadImage(file);

        host.setProfileImageUrl(imageUrl);

        userRepository.save(host);

        return getHostProfile(host.getId());
    }
    
    private final PropertyService propertyService;

    public HostProfileResponse getHostProfile(Long hostId) {

    	// 1. Find host
        User host = userRepository.findById(hostId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Host not found with id: " + hostId
                        )
                );

        // -----------------------------
        // HOST PROPERTIES
        // -----------------------------

        List<PropertyResponse> properties =
                propertyRepository
                        .findByOwnerId(hostId)
                        .stream()
                        .map(propertyService::convertToResponse)
                        .toList();

        long totalProperties =
                propertyRepository.countByOwnerId(hostId);


        // -----------------------------
        // HOST REVIEWS
        // -----------------------------

        List<Review> hostReviews =
                reviewRepository
                        .findByProperty_Owner_IdOrderByCreatedAtDesc(
                                hostId
                        );

        long totalReviews = hostReviews.size();


        // -----------------------------
        // AVERAGE RATING
        // -----------------------------

        double averageRating = hostReviews
                .stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);

        averageRating =
                Math.round(averageRating * 10.0) / 10.0;


        // -----------------------------
        // REVIEW RESPONSE
        // -----------------------------

        List<HostReviewResponse> reviews =
                hostReviews
                        .stream()
                        .map(review ->
                                new HostReviewResponse(
                                        review.getId(),

                                        review.getUser().getId(),

                                        review.getUser().getName(),

                                        review.getProperty().getId(),

                                        review.getProperty().getTitle(),

                                        review.getRating(),

                                        review.getComment(),

                                        review.getCreatedAt()
                                )
                        )
                        .toList();


        // -----------------------------
        // FINAL RESPONSE
        // -----------------------------

        return new HostProfileResponse(
                host.getId(),
                host.getName(),
                host.getEmail(),
                host.getPhone(),
                host.getProfileImageUrl(),
                host.getAbout(),
                host.getCreatedAt(),
                totalProperties,
                totalReviews,
                averageRating,
                reviews,
                properties
        );
             
    }
    
    public HostProfileResponse updateHostProfile(
            HostProfileUpdateRequest request) {

        User host = getCurrentUser();

        if (request.getName() != null &&
                !request.getName().trim().isEmpty()) {

            host.setName(request.getName().trim());
        }

        if (request.getPhone() != null) {
            host.setPhone(request.getPhone().trim());
        }

        if (request.getAbout() != null) {
            host.setAbout(request.getAbout().trim());
        }

        userRepository.save(host);

        return getHostProfile(host.getId());
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
                        )
                );
    }
    
    private final CloudinaryService cloudinaryService;
    
    
}