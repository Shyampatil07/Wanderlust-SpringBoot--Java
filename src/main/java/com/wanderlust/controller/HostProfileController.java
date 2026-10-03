package com.wanderlust.controller;

import com.wanderlust.dto.ApiResponse;
import com.wanderlust.dto.HostProfileResponse;
import com.wanderlust.dto.HostProfileUpdateRequest;
import com.wanderlust.service.HostProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(
        name = "Host Profile",
        description = "Host profile and statistics APIs"
)
@RestController
@RequestMapping("/api/hosts")
public class HostProfileController {

    private final HostProfileService hostProfileService;

    public HostProfileController(
            HostProfileService hostProfileService) {

        this.hostProfileService = hostProfileService;
    }

    @GetMapping("/{hostId}")
    @Operation(
            summary = "Get host profile",
            description =
                    "Returns host information, property count, " +
                    "review count and average rating"
    )
    public ResponseEntity<ApiResponse<HostProfileResponse>>
    getHostProfile(
            @PathVariable Long hostId) {

        HostProfileResponse response =
                hostProfileService.getHostProfile(hostId);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Host profile fetched successfully",
                        response
                )
        );
    }
    
    @PutMapping("/profile")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<HostProfileResponse>>
    updateHostProfile(
            @RequestBody HostProfileUpdateRequest request) {

        HostProfileResponse response =
                hostProfileService.updateHostProfile(request);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Host profile updated successfully",
                        response
                )
        );
    }
    
    @PostMapping(
            value = "/profile/photo",
            consumes = "multipart/form-data"
    )
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<HostProfileResponse>>
    uploadProfilePhoto(
            @RequestParam("file") MultipartFile file)
            throws IOException {

        HostProfileResponse response =
                hostProfileService.uploadProfilePhoto(file);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Profile photo uploaded successfully",
                        response
                )
        );
    }
    
    
    
}