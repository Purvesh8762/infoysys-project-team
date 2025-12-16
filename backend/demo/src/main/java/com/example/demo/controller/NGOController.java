package com.example.demo.controller;

import com.example.demo.repository.NGORepository;
import com.example.demo.entity.NGO;
import com.example.demo.service.CloudinaryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/ngos")
public class NGOController {

    private final NGORepository repo;
    private final CloudinaryService cloudinaryService;

    public NGOController(NGORepository repo, CloudinaryService cloudinaryService) {
        this.repo = repo;
        this.cloudinaryService = cloudinaryService;
    }

    // GET → /ngos
    @GetMapping
    public List<NGO> getAll() {
        return repo.findAll();
    }

    // POST → /ngos/add
    @PostMapping("/add")
    public ResponseEntity<?> addNGO(
            @RequestParam("ngoName") String ngoName,
            @RequestParam("ngoType") String ngoType,
            @RequestParam("registrationNumber") String registrationNumber,
            @RequestParam(value = "registrationCertificate", required = false) MultipartFile registrationCertificate,
            @RequestParam("contact") String contact,
            @RequestParam("email") String email,
            @RequestParam("address") String address,
            @RequestParam("state") String state,
            @RequestParam("district") String district,
            @RequestParam("city") String city,
            @RequestParam("pincode") String pincode,
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

            // Check if Registration Number already exists
            if (repo.existsByRegistrationNumber(registrationNumber)) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body("Registration Number already exists");
            }

            // Create new NGO entity
            NGO ngo = new NGO();
            ngo.setNgoName(ngoName);
            ngo.setNgoType(ngoType);
            ngo.setRegistrationNumber(registrationNumber);
            ngo.setContact(contact);
            ngo.setEmail(email);
            ngo.setAddress(address);
            ngo.setState(state);
            ngo.setDistrict(district);
            ngo.setCity(city);
            ngo.setPincode(pincode);

            // Handle latitude and longitude
            if (latitude != null && !latitude.trim().isEmpty()) {
                try {
                    ngo.setLatitude(Double.parseDouble(latitude));
                } catch (NumberFormatException e) {
                    return ResponseEntity
                            .status(HttpStatus.BAD_REQUEST)
                            .body("Invalid latitude value. Must be a number.");
                }
            }

            if (longitude != null && !longitude.trim().isEmpty()) {
                try {
                    ngo.setLongitude(Double.parseDouble(longitude));
                } catch (NumberFormatException e) {
                    return ResponseEntity
                            .status(HttpStatus.BAD_REQUEST)
                            .body("Invalid longitude value. Must be a number.");
                }
            }

            ngo.setPassword(password);

            // Handle Registration Certificate file upload to Cloudinary
            if (registrationCertificate != null && !registrationCertificate.isEmpty()) {
                try {
                    String registrationCertUrl = cloudinaryService.uploadFile(registrationCertificate, "ngos/registration-certificates");
                    ngo.setRegistrationCertificateUrl(registrationCertUrl);
                    ngo.setRegistrationCertificateFilename(registrationCertificate.getOriginalFilename());
                } catch (IllegalArgumentException e) {
                    return ResponseEntity
                            .status(HttpStatus.BAD_REQUEST)
                            .body("Registration Certificate: " + e.getMessage());
                } catch (IOException e) {
                    return ResponseEntity
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("Failed to upload Registration Certificate to Cloudinary: " + e.getMessage());
                }
            }

            // Save NGO
            NGO saved = repo.save(ngo);

            return ResponseEntity.ok(saved);

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error saving NGO: " + e.getMessage());
        }
    }
}

