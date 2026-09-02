package com.wanderlust.controller;



import com.wanderlust.dto.ApiResponse;
import com.wanderlust.dto.PropertyRequest;
import com.wanderlust.dto.PropertyResponse;
import com.wanderlust.service.PropertyService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.List;

@Tag(
        name = "Properties",
        description = "Property listing and management APIs"
)
@RestController
@RequestMapping("/api/properties")
//@SecurityRequirement(name = "bearerAuth")
public class PropertyController {

    private final PropertyService propertyService;

    public PropertyController(PropertyService propertyService) {
        this.propertyService = propertyService;
    }
    
    

    //CREATE 
    @PostMapping
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Create a property",
            description = "Creates a new property for the authenticated user"
    )
    public ResponseEntity<ApiResponse<PropertyResponse>>createProperty(
            @Valid @RequestBody PropertyRequest request) {

        PropertyResponse response =
                propertyService.createProperty(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        new ApiResponse<>(
                                true,
                                "Property created successfully",
                                response
                        )
                );
    }

 // GET ALL + SEARCH + FILTER + SORT
    @GetMapping
    public ResponseEntity <ApiResponse<List<PropertyResponse>>> getAllProperties(

            @RequestParam(required = false)
            String location,

            @RequestParam(required = false)
            Double minPrice,

            @RequestParam(required = false)
            Double maxPrice,

            @RequestParam(required = false)
            Integer guests,

            @RequestParam(required = false)
            String sort) {

        List<PropertyResponse> properties =
                propertyService.getAllProperties(
                        location,
                        minPrice,
                        maxPrice,
                        guests,
                        sort
                );

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Properties fetched successfully",
                        properties
                )
        );
    }

 // GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PropertyResponse>> getPropertyById(
            @PathVariable Long id) {

        PropertyResponse response =
                propertyService.getPropertyById(id);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Property fetched successfully",
                        response
                )
        );
    }

 // UPDATE
    @PutMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<PropertyResponse>> updateProperty(
            @PathVariable Long id,
            @Valid @RequestBody PropertyRequest request) {

        PropertyResponse response =
                propertyService.updateProperty(id, request);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Property updated successfully",
                        response
                )
        );
    }

 // DELETE
    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<Void>> deleteProperty(
            @PathVariable Long id) {

        propertyService.deleteProperty(id);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Property deleted successfully",
                        null
                )
        );
    }
    
 // IMAGE UPLOAD
    @PostMapping(
            value = "/{id}/image",
            consumes = "multipart/form-data"
    )
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<PropertyResponse>> uploadImage(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file)
            throws IOException {

        PropertyResponse response =
                propertyService.uploadPropertyImage(
                        id,
                        file
                );

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Image uploaded successfully",
                        response
                )
        );
    }
    
}