package com.wanderlust.controller;

import com.wanderlust.dto.ApiResponse;
import com.wanderlust.dto.ReviewRequest;
import com.wanderlust.dto.ReviewResponse;
import com.wanderlust.service.ReviewService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(
        name = "Reviews",
        description = "Property review APIs"
)
@RestController
@RequestMapping("/api/properties/{propertyId}/reviews")
//@SecurityRequirement(name = "bearerAuth")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(
            ReviewService reviewService) {

        this.reviewService = reviewService;
    }

    @PostMapping
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<ReviewResponse>> createReview(
            @PathVariable Long propertyId,
            @Valid @RequestBody ReviewRequest request) {

        ReviewResponse response =
                reviewService.createReview(
                        propertyId,
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        new ApiResponse<>(
                                true,
                                "Review created successfully",
                                response
                        )
                );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> getPropertyReviews(
            @PathVariable Long propertyId) {

        List<ReviewResponse> reviews =
                reviewService.getPropertyReviews(propertyId);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Reviews fetched successfully",
                        reviews
                )
        );
    }
    
    @PutMapping("/{reviewId}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<ReviewResponse>> updateReview(
            @PathVariable Long propertyId,
            @PathVariable Long reviewId,
            @Valid @RequestBody ReviewRequest request) {

        ReviewResponse response =
                reviewService.updateReview(
                        propertyId,
                        reviewId,
                        request
                );

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Review updated successfully",
                        response
                )
        );
    }
    
    @DeleteMapping("/{reviewId}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> deleteReview(
            @PathVariable Long propertyId,
            @PathVariable Long reviewId) {

        reviewService.deleteReview(
                propertyId,
                reviewId
        );

        return ResponseEntity.noContent().build();
    }
    
}