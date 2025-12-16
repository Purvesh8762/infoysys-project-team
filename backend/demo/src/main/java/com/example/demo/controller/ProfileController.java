package com.example.demo.controller;

import com.example.demo.entity.Citizen;
import com.example.demo.repository.CitizenRepository;
import com.example.demo.service.CloudinaryService;
import com.example.demo.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/profile")
@CrossOrigin(origins = "http://localhost:5173")
public class ProfileController {

    @Autowired
    private CitizenRepository citizenRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CloudinaryService cloudinaryService;

    @GetMapping("/me")
    public ResponseEntity<?> getProfile(HttpServletRequest request) {
        try {
            // Get token from Authorization header
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Authorization token required");
            }

            String token = authHeader.substring(7);
            
            // Validate token is not expired
            if (jwtUtil.isTokenExpired(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Token has expired. Please login again.");
            }
            
            // Extract email from token
            String email = jwtUtil.extractEmail(token);
            if (email == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid token");
            }

            // Extract role from token
            String role = jwtUtil.extractRole(token);
            if (role == null || !role.equals("CITIZEN")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("This endpoint is for citizens only");
            }
            
            // Validate token with email
            if (!jwtUtil.validateToken(token, email)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid token");
            }

            // Find citizen by email
            Citizen citizen = citizenRepository.findByEmail(email);
            if (citizen == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Citizen profile not found");
            }

            // Map entity to response DTO
            Map<String, Object> profileData = new HashMap<>();
            profileData.put("id", citizen.getId());
            profileData.put("fullName", citizen.getFullName());
            profileData.put("shortName", citizen.getFullName() != null && citizen.getFullName().contains(" ") 
                    ? citizen.getFullName().split(" ")[0] + " " + citizen.getFullName().split(" ")[citizen.getFullName().split(" ").length - 1]
                    : citizen.getFullName());
            profileData.put("aadhaar", citizen.getAadharNum());
            profileData.put("email", citizen.getEmail());
            profileData.put("mobile", citizen.getMobileNum());
            profileData.put("dob", citizen.getDateOfBirth() != null ? citizen.getDateOfBirth().toString() : null);
            profileData.put("state", citizen.getState());
            profileData.put("district", citizen.getDistrict());
            profileData.put("city", citizen.getCity());
            profileData.put("address", citizen.getAddress());
            profileData.put("role", "CITIZEN");
            profileData.put("photoUrl", citizen.getProfilePhotoUrl());

            return ResponseEntity.ok(profileData);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching profile: " + e.getMessage());
        }
    }

    @PutMapping(value = "/me", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> updateProfile(
            HttpServletRequest request,
            @RequestParam(value = "fullName", required = false) String fullName,
            @RequestParam(value = "aadhaar", required = false) String aadhaar,
            @RequestParam(value = "mobile", required = false) String mobile,
            @RequestParam(value = "dob", required = false) String dob,
            @RequestParam(value = "state", required = false) String state,
            @RequestParam(value = "district", required = false) String district,
            @RequestParam(value = "city", required = false) String city,
            @RequestParam(value = "address", required = false) String address,
            @RequestParam(value = "profilePhoto", required = false) MultipartFile profilePhoto) {
        try {
            // Get token from Authorization header
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Authorization token required");
            }

            String token = authHeader.substring(7);
            
            // Validate token is not expired
            if (jwtUtil.isTokenExpired(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Token has expired. Please login again.");
            }
            
            // Extract email from token
            String email = jwtUtil.extractEmail(token);
            if (email == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid token");
            }

            // Extract role from token
            String role = jwtUtil.extractRole(token);
            if (role == null || !role.equals("CITIZEN")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("This endpoint is for citizens only");
            }
            
            // Validate token with email
            if (!jwtUtil.validateToken(token, email)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid token");
            }

            // Find citizen by email
            Citizen citizen = citizenRepository.findByEmail(email);
            if (citizen == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Citizen profile not found");
            }

            // Update fields if provided
            if (fullName != null && !fullName.trim().isEmpty()) {
                citizen.setFullName(fullName.trim());
            }

            if (aadhaar != null && !aadhaar.trim().isEmpty()) {
                String aadhaarTrimmed = aadhaar.trim();
                // Check if Aadhaar already exists for another user
                if (!citizen.getAadharNum().equals(aadhaarTrimmed)) {
                    boolean aadhaarExists = citizenRepository.existsByAadharNum(aadhaarTrimmed);
                    if (aadhaarExists) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body("Aadhaar number already exists");
                    }
                }
                citizen.setAadharNum(aadhaarTrimmed);
            }

            if (mobile != null && !mobile.trim().isEmpty()) {
                citizen.setMobileNum(mobile.trim());
            }

            if (dob != null && !dob.trim().isEmpty()) {
                try {
                    java.time.LocalDate dobDate = java.time.LocalDate.parse(dob.trim());
                    citizen.setDateOfBirth(dobDate);
                } catch (Exception e) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("Invalid date format. Use YYYY-MM-DD");
                }
            }

            if (state != null && !state.trim().isEmpty()) {
                citizen.setState(state.trim());
            }

            if (district != null && !district.trim().isEmpty()) {
                citizen.setDistrict(district.trim());
            }

            if (city != null && !city.trim().isEmpty()) {
                citizen.setCity(city.trim());
            }

            if (address != null && !address.trim().isEmpty()) {
                citizen.setAddress(address.trim());
            }

            // Handle profile photo upload
            if (profilePhoto != null && !profilePhoto.isEmpty()) {
                try {
                    // Upload image to Cloudinary
                    String photoUrl = cloudinaryService.uploadImage(profilePhoto, "citizens/profile-photos");
                    citizen.setProfilePhotoUrl(photoUrl);
                } catch (IOException e) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("Failed to upload profile photo: " + e.getMessage());
                } catch (IllegalArgumentException e) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(e.getMessage());
                }
            }

            // Save updated citizen
            Citizen updatedCitizen = citizenRepository.save(citizen);

            // Map entity to response DTO
            Map<String, Object> profileData = new HashMap<>();
            profileData.put("id", updatedCitizen.getId());
            profileData.put("fullName", updatedCitizen.getFullName());
            profileData.put("shortName", updatedCitizen.getFullName() != null && updatedCitizen.getFullName().contains(" ") 
                    ? updatedCitizen.getFullName().split(" ")[0] + " " + updatedCitizen.getFullName().split(" ")[updatedCitizen.getFullName().split(" ").length - 1]
                    : updatedCitizen.getFullName());
            profileData.put("aadhaar", updatedCitizen.getAadharNum());
            profileData.put("email", updatedCitizen.getEmail());
            profileData.put("mobile", updatedCitizen.getMobileNum());
            profileData.put("dob", updatedCitizen.getDateOfBirth() != null ? updatedCitizen.getDateOfBirth().toString() : null);
            profileData.put("state", updatedCitizen.getState());
            profileData.put("district", updatedCitizen.getDistrict());
            profileData.put("city", updatedCitizen.getCity());
            profileData.put("address", updatedCitizen.getAddress());
            profileData.put("role", "CITIZEN");
            profileData.put("photoUrl", updatedCitizen.getProfilePhotoUrl());

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Profile updated successfully");
            response.put("data", profileData);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating profile: " + e.getMessage());
        }
    }
}

