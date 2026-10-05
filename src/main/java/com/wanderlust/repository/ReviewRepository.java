package com.wanderlust.repository;

import com.wanderlust.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository
        extends JpaRepository<Review, Long> {	

    List<Review> findByPropertyId(Long propertyId);

    List<Review> findByUserId(Long userId);

    boolean existsByUserIdAndPropertyId(
            Long userId,
            Long propertyId
    );

    long countByPropertyId(Long propertyId);

    @Query("""
            SELECT AVG(r.rating)
            FROM Review r
            WHERE r.property.id = :propertyId
            """)
    Double findAverageRatingByPropertyId(
            @Param("propertyId") Long propertyId
    );
    
    @Query("""
            SELECT COUNT(r)
            FROM Review r
            WHERE r.property.owner.id = :ownerId
            """)
    long countReviewsByHostId(@Param("ownerId") Long ownerId);

    @Query("""
            SELECT AVG(r.rating)
            FROM Review r
            WHERE r.property.owner.id = :ownerId
            """)
    Double findAverageRatingByHostId(@Param("ownerId") Long ownerId);
    
    List<Review> findByProperty_Owner_IdOrderByCreatedAtDesc(
            Long ownerId
    );
    
    
}