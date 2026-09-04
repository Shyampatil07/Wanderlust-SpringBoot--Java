package com.wanderlust.specification;

import com.wanderlust.entity.Property;
import org.springframework.data.jpa.domain.Specification;

public class PropertySpecification {

    public static Specification<Property> hasLocation(String location) {
        return (root, query, cb) -> {
            if (location == null || location.isBlank()) {
                return null;
            }

            return cb.like(
                    cb.lower(root.get("location")),
                    "%" + location.toLowerCase() + "%"
            );
        };
    }

    public static Specification<Property> minPrice(Double minPrice) {
        return (root, query, cb) -> {
            if (minPrice == null) {
                return null;
            }

            return cb.greaterThanOrEqualTo(
                    root.get("pricePerNight"),
                    minPrice
            );
        };
    }

    public static Specification<Property> maxPrice(Double maxPrice) {
        return (root, query, cb) -> {
            if (maxPrice == null) {
                return null;
            }

            return cb.lessThanOrEqualTo(
                    root.get("pricePerNight"),
                    maxPrice
            );
        };
    }

    public static Specification<Property> minGuests(Integer guests) {
        return (root, query, cb) -> {
            if (guests == null) {
                return null;
            }

            return cb.greaterThanOrEqualTo(
                    root.get("maxGuests"),
                    guests
            );
        };
    }
}