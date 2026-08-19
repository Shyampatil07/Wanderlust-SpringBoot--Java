package com.wanderlust.service;

import com.wanderlust.dto.PropertyRequest;
import com.wanderlust.dto.PropertyResponse;
import com.wanderlust.entity.Property;
import com.wanderlust.entity.User;
import com.wanderlust.exception.ResourceNotFoundException;
import com.wanderlust.repository.PropertyRepository;
import com.wanderlust.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PropertyService {

    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;

    public PropertyService(PropertyRepository propertyRepository,
                           UserRepository userRepository) {
        this.propertyRepository = propertyRepository;
        this.userRepository = userRepository;
    }

    public PropertyResponse createProperty(PropertyRequest request) {

        User owner = userRepository.findById(request.getOwnerId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Owner not found with id: " + request.getOwnerId()
                        ));

        Property property = new Property();

        property.setTitle(request.getTitle());
        property.setDescription(request.getDescription());
        property.setLocation(request.getLocation());
        property.setPricePerNight(request.getPricePerNight());
        property.setMaxGuests(request.getMaxGuests());
        property.setOwner(owner);

        Property savedProperty = propertyRepository.save(property);

        return convertToResponse(savedProperty);
    }

    public List<PropertyResponse> getAllProperties() {

        return propertyRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    public PropertyResponse getPropertyById(Long id) {

        Property property = propertyRepository.findById(id)
        		.orElseThrow(() ->
                new ResourceNotFoundException(
                        "Property not found with id: " + id
                ));

        return convertToResponse(property);
    }

    public PropertyResponse updateProperty(
            Long id,
            PropertyRequest request) {

        Property property = propertyRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Property not found with id: " + id
                        ));

        property.setTitle(request.getTitle());
        property.setDescription(request.getDescription());
        property.setLocation(request.getLocation());
        property.setPricePerNight(request.getPricePerNight());
        property.setMaxGuests(request.getMaxGuests());

        Property updatedProperty =
                propertyRepository.save(property);

        return convertToResponse(updatedProperty);
    }

    public void deleteProperty(Long id) {

        if (!propertyRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "Property not found with id: " + id
            );
        }

        propertyRepository.deleteById(id);
    }

    private PropertyResponse convertToResponse(Property property) {

        return new PropertyResponse(
        		
                property.getId(),
                property.getTitle(),
                property.getDescription(),
                property.getLocation(),
                property.getPricePerNight(),
                property.getMaxGuests(),
                property.getOwner().getId()
        );
    }
}