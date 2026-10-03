package com.wanderlust.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class HostProfileResponse {

    private Long hostId;

    private String name;

    private String email;

    private String phone;

    private String profileImageUrl;

    private String about;

    private LocalDateTime hostingSince;

    private Long totalProperties;

    private Long totalReviews;

    private Double averageRating;

    private List<HostReviewResponse> reviews;

    private List<PropertyResponse> properties;
}