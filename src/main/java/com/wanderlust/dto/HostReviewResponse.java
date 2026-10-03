package com.wanderlust.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class HostReviewResponse {

    private Long id;

    private Long guestId;

    private String guestName;

    private Long propertyId;

    private String propertyTitle;

    private Integer rating;

    private String comment;

    private LocalDateTime createdAt;
}