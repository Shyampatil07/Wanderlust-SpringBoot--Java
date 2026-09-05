package com.wanderlust.service;
import com.wanderlust.dto.PropertyRequest;
import com.wanderlust.dto.PropertyResponse;
import com.wanderlust.entity.Property;
import com.wanderlust.entity.Role;
import com.wanderlust.entity.User;
import com.wanderlust.exception.ResourceNotFoundException;
import com.wanderlust.repository.PropertyRepository;
import com.wanderlust.repository.UserRepository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PropertyServiceTest {

    @Mock
    private PropertyRepository propertyRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CloudinaryService cloudinaryService;

    @InjectMocks
    private PropertyService propertyService;

//    @Test
//    void shouldGetAllProperties() {
//
//        // Arrange
//        Property property = new Property();
//
//        property.setId(1L);
//        property.setTitle("Pune Villa");
//        property.setDescription("Beautiful villa");
//        property.setLocation("Pune");
//        property.setPricePerNight(5000.0);
//        property.setMaxGuests(4);
//
//        when(propertyRepository.findAll(
//                ArgumentMatchers.<Specification<Property>>any(),
//                any(Sort.class)
//        )).thenReturn(List.of(property));
//
//
//        // Act
//        List<PropertyResponse> result =
//                propertyService.getAllProperties(
//                        null,
//                        null,
//                        null,
//                        null,
//                        null
//                );
//
//
//        // Assert
//        assertEquals(1, result.size());
//
//        assertEquals(
//                "Pune Villa",
//                result.get(0).getTitle()
//        );
//
//        assertEquals(
//                "Pune",
//                result.get(0).getLocation()
//        );
//    }
    
//    @Test
//    void shouldThrowExceptionWhenPropertyNotFound() {
//
//        // Arrange
//        when(propertyRepository.findById(99L))
//                .thenReturn(java.util.Optional.empty());
//
//
//        // Act + Assert
//        assertThrows(
//                ResourceNotFoundException.class,
//                () -> propertyService.getPropertyById(99L)
//        );
//
//
//        verify(propertyRepository)
//                .findById(99L);
//    }
//    
//    @Test
//    void shouldCreateProperty() {
//
//        // Arrange
//
//        User user = new User();
//
//        user.setId(1L);
//        user.setName("Shyam");
//        user.setEmail("shyam@gmail.com");
//
//
//        PropertyRequest request =
//                new PropertyRequest();
//
//        request.setTitle("Pune Villa");
//        request.setDescription("Beautiful villa");
//        request.setLocation("Pune");
//        request.setPricePerNight(5000.0);
//        request.setMaxGuests(4);
//
//
//        Authentication authentication =
//                mock(Authentication.class);
//
//        when(authentication.getName())
//                .thenReturn("shyam@gmail.com");
//
//
//        SecurityContext securityContext =
//                mock(SecurityContext.class);
//
//        when(securityContext.getAuthentication())
//                .thenReturn(authentication);
//
//
//        SecurityContextHolder
//                .setContext(securityContext);
//
//
//        when(userRepository.findByEmail(
//                "shyam@gmail.com"
//        )).thenReturn(
//                Optional.of(user)
//        );
//
//
//        Property savedProperty =
//                new Property();
//
//        savedProperty.setId(1L);
//        savedProperty.setTitle("Pune Villa");
//        savedProperty.setDescription("Beautiful villa");
//        savedProperty.setLocation("Pune");
//        savedProperty.setPricePerNight(5000.0);
//        savedProperty.setMaxGuests(4);
//        savedProperty.setOwner(user);
//
//
//        when(propertyRepository.save(any(Property.class)))
//                .thenReturn(savedProperty);
//
//
//        // Act
//
//        PropertyResponse response =
//                propertyService.createProperty(request);
//
//
//        // Assert
//
//        assertNotNull(response);
//
//        assertEquals(
//                1L,
//                response.getId()
//        );
//
//        assertEquals(
//                "Pune Villa",
//                response.getTitle()
//        );
//
//        assertEquals(
//                "Pune",
//                response.getLocation()
//        );
//
//
//        verify(propertyRepository)
//                .save(any(Property.class));
//
//
//        verify(userRepository)
//                .findByEmail("shyam@gmail.com");
//
//
//        SecurityContextHolder.clearContext();
//    }
    
//    @Test
//    void shouldNotAllowNonOwnerToUpdateProperty() {
//
//        // ---------- Arrange ----------
//
//        User owner = new User();
//        owner.setId(1L);
//        owner.setEmail("owner@gmail.com");
//
//        User currentUser = new User();
//        currentUser.setId(2L);
//        currentUser.setEmail("user@gmail.com");
//
//
//        Property property = new Property();
//        property.setId(10L);
//        property.setTitle("Pune Villa");
//        property.setOwner(owner);
//
//
//        when(propertyRepository.findById(10L))
//                .thenReturn(Optional.of(property));
//
//
//        // Mock logged-in user
//        Authentication authentication =
//                mock(Authentication.class);
//
//        when(authentication.getName())
//                .thenReturn("user@gmail.com");
//
//        SecurityContext securityContext =
//                mock(SecurityContext.class);
//
//        when(securityContext.getAuthentication())
//                .thenReturn(authentication);
//
//        SecurityContextHolder.setContext(securityContext);
//
//
//        when(userRepository.findByEmail("user@gmail.com"))
//                .thenReturn(Optional.of(currentUser));
//
//
//        PropertyRequest request = new PropertyRequest();
//
//        request.setTitle("Updated Villa");
//        request.setDescription("Updated description");
//        request.setLocation("Mumbai");
//        request.setPricePerNight(6000.0);
//        request.setMaxGuests(5);
//
//
//        // ---------- Act + Assert ----------
//
//        assertThrows(
//                AccessDeniedException.class,
//                () -> propertyService.updateProperty(
//                        10L,
//                        request
//                )
//        );
//
//
//        // ---------- Verify ----------
//
//        verify(propertyRepository)
//                .findById(10L);
//
//        verify(propertyRepository, never())
//                .save(any(Property.class));
//    }
    
//    @Test
//    void shouldAllowOwnerToUpdateProperty() {
//
//        // ---------- Arrange ----------
//
//        User owner = new User();
//        owner.setId(1L);
//        owner.setEmail("owner@gmail.com");
//        owner.setRole(Role.USER);
//
//
//        Property property = new Property();
//
//        property.setId(10L);
//        property.setTitle("Old Villa");
//        property.setDescription("Old description");
//        property.setLocation("Pune");
//        property.setPricePerNight(4000.0);
//        property.setMaxGuests(4);
//        property.setOwner(owner);
//
//
//        when(propertyRepository.findById(10L))
//                .thenReturn(Optional.of(property));
//
//
//        Authentication authentication =
//                mock(Authentication.class);
//
//        when(authentication.getName())
//                .thenReturn("owner@gmail.com");
//
//
//        SecurityContext securityContext =
//                mock(SecurityContext.class);
//
//        when(securityContext.getAuthentication())
//                .thenReturn(authentication);
//
//        SecurityContextHolder.setContext(securityContext);
//
//
//        when(userRepository.findByEmail("owner@gmail.com"))
//                .thenReturn(Optional.of(owner));
//
//
//        PropertyRequest request = new PropertyRequest();
//
//        request.setTitle("Updated Villa");
//        request.setDescription("Updated description");
//        request.setLocation("Mumbai");
//        request.setPricePerNight(6000.0);
//        request.setMaxGuests(5);
//
//
//        when(propertyRepository.save(any(Property.class)))
//                .thenAnswer(invocation ->
//                        invocation.getArgument(0));
//
//
//        // ---------- Act ----------
//
//        PropertyResponse response =
//                propertyService.updateProperty(
//                        10L,
//                        request
//                );
//
//
//        // ---------- Assert ----------
//
//        assertNotNull(response);
//
//        assertEquals(
//                "Updated Villa",
//                response.getTitle()
//        );
//
//        assertEquals(
//                "Mumbai",
//                response.getLocation()
//        );
//
//        assertEquals(
//                6000.0,
//                response.getPricePerNight()
//        );
//
//        assertEquals(
//                5,
//                response.getMaxGuests()
//        );
//
//
//        // ---------- Verify ----------
//
//        verify(propertyRepository)
//                .save(any(Property.class));
//    }
    
    @Test
    void shouldAllowAdminToUpdateAnyProperty() {

        // ---------- Arrange ----------

        User owner = new User();

        owner.setId(1L);
        owner.setEmail("owner@gmail.com");
        owner.setRole(Role.USER);


        User admin = new User();

        admin.setId(99L);
        admin.setEmail("admin@gmail.com");
        admin.setRole(Role.ADMIN);


        Property property = new Property();

        property.setId(10L);
        property.setTitle("Old Villa");
        property.setOwner(owner);


        when(propertyRepository.findById(10L))
                .thenReturn(Optional.of(property));


        // Logged-in user = ADMIN
        Authentication authentication =
                mock(Authentication.class);

        when(authentication.getName())
                .thenReturn("admin@gmail.com");


        SecurityContext securityContext =
                mock(SecurityContext.class);

        when(securityContext.getAuthentication())
                .thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);


        when(userRepository.findByEmail("admin@gmail.com"))
                .thenReturn(Optional.of(admin));


        PropertyRequest request = new PropertyRequest();

        request.setTitle("Admin Updated Villa");
        request.setDescription("Updated by admin");
        request.setLocation("Pune");
        request.setPricePerNight(7000.0);
        request.setMaxGuests(6);


        when(propertyRepository.save(any(Property.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));


        // ---------- Act ----------

        PropertyResponse response =
                propertyService.updateProperty(
                        10L,
                        request
                );


        // ---------- Assert ----------

        assertNotNull(response);

        assertEquals(
                "Admin Updated Villa",
                response.getTitle()
        );


        // ---------- Verify ----------

        verify(propertyRepository)
                .save(any(Property.class));
    }
    
    @AfterEach
    void cleanup() {
        SecurityContextHolder.clearContext();
    }
    
}