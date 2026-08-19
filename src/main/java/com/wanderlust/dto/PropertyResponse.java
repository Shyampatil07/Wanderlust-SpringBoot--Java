package com.wanderlust.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

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
    private Long ownerId;
}