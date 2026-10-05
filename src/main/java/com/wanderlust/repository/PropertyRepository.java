package com.wanderlust.repository;

import com.wanderlust.entity.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface PropertyRepository
        extends JpaRepository<Property, Long>,
                JpaSpecificationExecutor<Property> {

    List<Property> findByOwnerId(Long ownerId);
    
    long countByOwnerId(Long ownerId);
}