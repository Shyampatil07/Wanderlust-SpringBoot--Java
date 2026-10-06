	package com.wanderlust.service;

import com.wanderlust.dto.PropertyImageResponse;
import com.wanderlust.dto.PropertyRequest;
import com.wanderlust.dto.PropertyResponse;
import com.wanderlust.entity.Property;
import com.wanderlust.entity.PropertyImage;
import com.wanderlust.entity.User;
import com.wanderlust.exception.ResourceNotFoundException;
import com.wanderlust.repository.PropertyRepository;
import com.wanderlust.repository.UserRepository;
import com.wanderlust.specification.PropertySpecification;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.wanderlust.exception.ResourceNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;

import com.wanderlust.repository.BookingRepository;
import com.wanderlust.repository.PropertyImageRepository;

import java.io.IOException;
import java.util.List;

@Service
public class PropertyService {

    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;
    private final CloudinaryService cloudinaryService;
    private final BookingRepository bookingRepository;
    private final PropertyImageRepository propertyImageRepository;
    
    public PropertyService(
            PropertyRepository propertyRepository,
            UserRepository userRepository,
            CloudinaryService cloudinaryService,
            BookingRepository bookingRepository,
            PropertyImageRepository propertyImageRepository) {

        this.propertyRepository = propertyRepository;
        this.userRepository = userRepository;
        this.cloudinaryService = cloudinaryService;
        this.bookingRepository = bookingRepository;
        this.propertyImageRepository = propertyImageRepository;
    }

    public PropertyResponse uploadPropertyImage(
            Long propertyId,
            MultipartFile file) throws IOException {

        Property property =
                propertyRepository.findById(propertyId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Property not found with id: "
                                                + propertyId
                                ));

        User currentUser = getCurrentUser();

        boolean isAdmin =
                currentUser.getRole() != null
                        && currentUser.getRole()
                        .name()
                        .equals("ADMIN");

        boolean isOwner =
                property.getOwner().getId()
                        .equals(currentUser.getId());

        if (!isOwner && !isAdmin) {

            throw new AccessDeniedException(
                    "You are not allowed to update this property"
            );
        }

        if (file == null || file.isEmpty()) {

            throw new IllegalArgumentException(
                    "Image file is required"
            );
        }

        String imageUrl =
                cloudinaryService.uploadImage(file);

        property.setImageUrl(imageUrl);

        Property savedProperty =
                propertyRepository.save(property);

        return convertToResponse(savedProperty);
    }
    
    public PropertyResponse uploadPropertyImages(
            Long propertyId,
            List<MultipartFile> files) throws IOException {

        Property property =
                propertyRepository.findById(propertyId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Property not found with id: "
                                                + propertyId
                                ));

        User currentUser = getCurrentUser();

        boolean isAdmin =
                currentUser.getRole() != null
                        && currentUser.getRole()
                        .name()
                        .equals("ADMIN");

        boolean isOwner =
                property.getOwner().getId()
                        .equals(currentUser.getId());

        if (!isOwner && !isAdmin) {
            throw new AccessDeniedException(
                    "You are not allowed to update this property"
            );
        }

        if (files == null || files.isEmpty()) {
            throw new IllegalArgumentException(
                    "At least one image is required"
            );
        }

        for (MultipartFile file : files) {

            if (file == null || file.isEmpty()) {
                continue;
            }

            if (file.getContentType() == null ||
                    !file.getContentType().startsWith("image/")) {

                throw new IllegalArgumentException(
                        "Only image files are allowed"
                );
            }

            String imageUrl =
                    cloudinaryService.uploadImage(file);

            PropertyImage propertyImage =
                    new PropertyImage();

            propertyImage.setImageUrl(imageUrl);
            propertyImage.setProperty(property);

            propertyImageRepository.save(propertyImage);
        }

        return convertToResponse(property);
    }

    public PropertyResponse createProperty(PropertyRequest request) {

    	 User owner = getCurrentUser();

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

    public List<PropertyResponse> getAllProperties(
            String location,
            Double minPrice,
            Double maxPrice,
            Integer guests,
            String sort) {

        Specification<Property> specification =
                Specification
                        .where(PropertySpecification.hasLocation(location))
                        .and(PropertySpecification.minPrice(minPrice))
                        .and(PropertySpecification.maxPrice(maxPrice))
                        .and(PropertySpecification.minGuests(guests));

        Sort sorting = Sort.unsorted();

        if ("priceAsc".equalsIgnoreCase(sort)) {

            sorting = Sort.by(
                    Sort.Direction.ASC,
                    "pricePerNight"
            );

        } else if ("priceDesc".equalsIgnoreCase(sort)) {

            sorting = Sort.by(
                    Sort.Direction.DESC,
                    "pricePerNight"
            );
        }

        return propertyRepository
                .findAll(specification, sorting)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }
    
    public List<PropertyResponse> getMyProperties() {

        User currentUser = getCurrentUser();

        return propertyRepository
                .findByOwnerId(currentUser.getId())
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

        User currentUser = getCurrentUser();

        // Admin can update any property
        if (!isAdmin(currentUser)) {

            // Normal user can update only their own property
            if (!property.getOwner().getId()
                    .equals(currentUser.getId())) {

                throw new AccessDeniedException(
                        "You are not allowed to update this property"
                );
            }
        }

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

        Property property = propertyRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Property not found with id: " + id
                        ));

        User currentUser = getCurrentUser();

        // Admin can delete any property
        if (!isAdmin(currentUser)) {

            // User can delete only their own property
            if (!property.getOwner().getId()
                    .equals(currentUser.getId())) {

                throw new AccessDeniedException(
                        "You are not allowed to delete this property"
                );
            }
        }

        // Do not delete property if bookings exist
        if (bookingRepository.existsByPropertyId(id)) {

            throw new IllegalStateException(
                    "This property cannot be deleted because it has existing bookings."
            );
        }

        propertyRepository.delete(property);
    }
    
    public PropertyResponse convertToResponse(Property property) {

    	List<PropertyImageResponse> images =
    	        property.getImages() == null
    	                ? List.of()
    	                : property.getImages()
    	                        .stream()
    	                        .map(image ->
    	                                new PropertyImageResponse(
    	                                        image.getId(),
    	                                        image.getImageUrl()
    	                                )
    	                        )
    	                        .toList();

    	return new PropertyResponse(
    	        property.getId(),
    	        property.getTitle(),
    	        property.getDescription(),
    	        property.getLocation(),
    	        property.getPricePerNight(),
    	        property.getMaxGuests(),
    	        property.getImageUrl(),
    	        images,
    	        property.getOwner().getId(),
    	        property.getOwner().getName(),
    	        property.getOwner().getEmail()
    	);
    }
    
    private User getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Authenticated user not found"
                        ));
    }
    
    private boolean isAdmin(User user) {

        return user.getRole() != null
                && user.getRole().name().equals("ADMIN");
    }
    
    public void deletePropertyImage(Long propertyId, Long imageId) {

        Property property =
                propertyRepository.findById(propertyId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Property not found with id: " + propertyId
                                ));

        User currentUser = getCurrentUser();

        boolean isAdmin =
                currentUser.getRole() != null
                        && currentUser.getRole()
                        .name()
                        .equals("ADMIN");

        boolean isOwner =
                property.getOwner()
                        .getId()
                        .equals(currentUser.getId());

        if (!isOwner && !isAdmin) {
            throw new AccessDeniedException(
                    "You are not allowed to update this property"
            );
        }

        PropertyImage image =
                propertyImageRepository.findById(imageId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Property image not found with id: "
                                                + imageId
                                ));

        if (!image.getProperty()
                .getId()
                .equals(propertyId)) {

            throw new AccessDeniedException(
                    "This image does not belong to this property"
            );
        }

        propertyImageRepository.delete(image);
    }
    
}