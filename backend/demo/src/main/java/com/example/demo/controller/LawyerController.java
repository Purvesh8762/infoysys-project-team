package com.example.demo.controller;

import com.example.demo.repository.LawyerRepository;
import com.example.demo.entity.Lawyer;
import com.example.demo.service.CloudinaryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/lawyers")
public class LawyerController {

    private final LawyerRepository repo;
    private final CloudinaryService cloudinaryService;

    public LawyerController(LawyerRepository repo, CloudinaryService cloudinaryService) {
        this.repo = repo;
        this.cloudinaryService = cloudinaryService;
    }

    // GET → /lawyers
    @GetMapping
    public List<Lawyer> getAll() {
        return repo.findAll();
    }

    // POST → /lawyers/add
    @PostMapping("/add")
    public ResponseEntity<?> addLawyer(
            @RequestParam("fullName") String fullName,
            @RequestParam("email") String email,
            @RequestParam("phone") String phone,
            @RequestParam("aadhar") String aadhar,
            @RequestParam(value = "aadharProof", required = false) MultipartFile aadharProof,
            @RequestParam("barId") String barId,
            @RequestParam("barState") String barState,
            @RequestParam("specialization") String specialization,
            @RequestParam(value = "barCert", required = false) MultipartFile barCert,
            @RequestParam("experience") String experience,
            @RequestParam("address") String address,
            @RequestParam("district") String district,
            @RequestParam("city") String city,
            @RequestParam("state") String state,
            @RequestParam(value = "latitude", required = false) String latitude,
            @RequestParam(value = "longitude", required = false) String longitude,
            @RequestParam("password") String password
    ) {
        try {
            // Check if email already exists
            if (repo.existsByEmail(email)) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body("Email already exists");
            }

            // Check if Aadhar already exists
            if (repo.existsByAadharNum(aadhar)) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body("Aadhar number already exists");
            }

            // Check if Bar Council ID already exists
            if (repo.existsByBarCouncilId(barId)) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body("Bar Council ID already exists");
            }

            // Create new Lawyer entity
            Lawyer lawyer = new Lawyer();
            lawyer.setFullName(fullName);
            lawyer.setEmail(email);
            lawyer.setMobileNum(phone);
            lawyer.setAadharNum(aadhar);
            lawyer.setBarCouncilId(barId);
            lawyer.setBarState(barState);
            lawyer.setSpecialization(specialization);
            lawyer.setExperienceYears(Integer.parseInt(experience));
            lawyer.setAddress(address);
            lawyer.setDistrict(district);
            lawyer.setCity(city);
            lawyer.setState(state);
            
            // Handle latitude and longitude
            if (latitude != null && !latitude.trim().isEmpty()) {
                try {
                    lawyer.setLatitude(Double.parseDouble(latitude));
                } catch (NumberFormatException e) {
                    return ResponseEntity
                            .status(HttpStatus.BAD_REQUEST)
                            .body("Invalid latitude value. Must be a number.");
                }
            }
            
            if (longitude != null && !longitude.trim().isEmpty()) {
                try {
                    lawyer.setLongitude(Double.parseDouble(longitude));
                } catch (NumberFormatException e) {
                    return ResponseEntity
                            .status(HttpStatus.BAD_REQUEST)
                            .body("Invalid longitude value. Must be a number.");
                }
            }
            
            lawyer.setPassword(password);

            // Handle Aadhar Proof file upload to Cloudinary
            if (aadharProof != null && !aadharProof.isEmpty()) {
                try {
                    String aadharProofUrl = cloudinaryService.uploadFile(aadharProof, "lawyers/aadhar-proof");
                    lawyer.setAadharProofUrl(aadharProofUrl);
                    lawyer.setAadharProofFilename(aadharProof.getOriginalFilename());
                } catch (IllegalArgumentException e) {
                    return ResponseEntity
                            .status(HttpStatus.BAD_REQUEST)
                            .body("Aadhar Proof: " + e.getMessage());
                } catch (IOException e) {
                    return ResponseEntity
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("Failed to upload Aadhar Proof to Cloudinary: " + e.getMessage());
                }
            }

            // Handle Bar Certificate file upload to Cloudinary
            if (barCert != null && !barCert.isEmpty()) {
                try {
                    String barCertUrl = cloudinaryService.uploadFile(barCert, "lawyers/bar-certificates");
                    lawyer.setBarCertificateUrl(barCertUrl);
                    lawyer.setBarCertificateFilename(barCert.getOriginalFilename());
                } catch (IllegalArgumentException e) {
                    return ResponseEntity
                            .status(HttpStatus.BAD_REQUEST)
                            .body("Bar Certificate: " + e.getMessage());
                } catch (IOException e) {
                    return ResponseEntity
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("Failed to upload Bar Certificate to Cloudinary: " + e.getMessage());
                }
            }

            // Save lawyer
            Lawyer saved = repo.save(lawyer);

            return ResponseEntity.ok(saved);

        } catch (NumberFormatException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Invalid experience value. Must be a number.");
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error saving lawyer: " + e.getMessage());
        }
    }
}

