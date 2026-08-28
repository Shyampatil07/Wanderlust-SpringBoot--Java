package com.wanderlust.controller;

import com.wanderlust.dto.ReviewRequest;
import com.wanderlust.dto.ReviewResponse;
import com.wanderlust.service.ReviewService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/properties/{propertyId}/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(
            ReviewService reviewService) {

        this.reviewService = reviewService;
    }

    @PostMapping
    public ResponseEntity<ReviewResponse> createReview(
            @PathVariable Long propertyId,
            @Valid @RequestBody ReviewRequest request) {

        ReviewResponse response =
                reviewService.createReview(
                        propertyId,
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<ReviewResponse>> getPropertyReviews(
            @PathVariable Long propertyId) {

        return ResponseEntity.ok(
                reviewService.getPropertyReviews(propertyId)
        );
    }
    
    @PutMapping("/{reviewId}")
    public ResponseEntity<ReviewResponse> updateReview(
            @PathVariable Long propertyId,
            @PathVariable Long reviewId,
            @Valid @RequestBody ReviewRequest request) {

        ReviewResponse response =
                reviewService.updateReview(
                        propertyId,
                        reviewId,
                        request
                );

        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{reviewId}")
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