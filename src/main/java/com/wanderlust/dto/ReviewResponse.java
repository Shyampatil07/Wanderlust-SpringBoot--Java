package com.wanderlust.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ReviewResponse {

    private Long id;

    private Long userId;

    private String userName;

    private Long propertyId;

    private Integer rating;

    private String comment;

    private LocalDateTime createdAt;
}