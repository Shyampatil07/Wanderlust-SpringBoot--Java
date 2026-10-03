package com.wanderlust.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.List;

@Data
@Getter
@AllArgsConstructor
public class PropertyResponse {

    private Long id;

    private String title;

    private String description;

    private String location;

    private Double pricePerNight;

    private Integer maxGuests;

    // Keep this temporarily for old frontend compatibility
    private String imageUrl;

    // New multiple images
    private List<PropertyImageResponse> images;

    private Long ownerId;

    private String ownerName;

    private String ownerEmail;
}