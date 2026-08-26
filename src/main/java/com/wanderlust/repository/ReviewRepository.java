package com.wanderlust.repository;

import com.wanderlust.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository
        extends JpaRepository<Review, Long> {

    List<Review> findByPropertyId(Long propertyId);

    List<Review> findByUserId(Long userId);

    boolean existsByUserIdAndPropertyId(
            Long userId,
            Long propertyId
    );
}