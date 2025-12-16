package com.example.demo.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;

    @Autowired
    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    /**
     * Upload a PDF file to Cloudinary
     * @param file The multipart file to upload
     * @param folder Optional folder name in Cloudinary (e.g., "lawyers/aadhar-proof")
     * @return The public URL of the uploaded file
     * @throws IOException if upload fails
     */
    public String uploadFile(MultipartFile file, String folder) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be null or empty");
        }

        // Validate file type - only PDF
        String contentType = file.getContentType();
        if (contentType == null || !contentType.equals("application/pdf")) {
            throw new IllegalArgumentException("Only PDF files are allowed. Received: " + contentType);
        }

        // Validate file size - max 10MB
        long maxSize = 10 * 1024 * 1024; // 10MB
        if (file.getSize() > maxSize) {
            throw new IllegalArgumentException("File size must be less than 10MB");
        }

        try {
            // Upload options
            Map<String, Object> uploadOptions = ObjectUtils.asMap(
                "resource_type", "raw", // Use "raw" for PDF files
                "folder", folder != null ? folder : "lawyers",
                "use_filename", true,
                "unique_filename", true,
                "overwrite", false
            );

            // Upload file to Cloudinary
            Map<?, ?> uploadResult = cloudinary.uploader().upload(
                file.getBytes(),
                uploadOptions
            );

            // Return the secure URL
            return (String) uploadResult.get("secure_url");

        } catch (IOException e) {
            throw new IOException("Failed to upload file to Cloudinary: " + e.getMessage(), e);
        }
    }

    /**
     * Upload an image file to Cloudinary
     * @param file The multipart file to upload
     * @param folder Optional folder name in Cloudinary (e.g., "citizens/profile-photos")
     * @return The public URL of the uploaded image
     * @throws IOException if upload fails
     */
    public String uploadImage(MultipartFile file, String folder) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be null or empty");
        }

        // Validate file type - only images
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Only image files are allowed. Received: " + contentType);
        }

        // Validate file size - max 5MB
        long maxSize = 5 * 1024 * 1024; // 5MB
        if (file.getSize() > maxSize) {
            throw new IllegalArgumentException("Image size must be less than 5MB");
        }

        try {
            // Upload options for images
            Map<String, Object> uploadOptions = ObjectUtils.asMap(
                "resource_type", "image", // Use "image" for image files
                "folder", folder != null ? folder : "citizens/profile-photos",
                "use_filename", true,
                "unique_filename", true,
                "overwrite", false
            );

            // Upload file to Cloudinary
            Map<?, ?> uploadResult = cloudinary.uploader().upload(
                file.getBytes(),
                uploadOptions
            );

            // Return the secure URL
            return (String) uploadResult.get("secure_url");

        } catch (IOException e) {
            throw new IOException("Failed to upload image to Cloudinary: " + e.getMessage(), e);
        }
    }

    /**
     * Delete a file from Cloudinary using its public ID or URL
     * @param publicIdOrUrl The public ID or URL of the file to delete
     * @return true if deletion was successful
     */
    public boolean deleteFile(String publicIdOrUrl) {
        try {
            // Extract public ID from URL if URL is provided
            String publicId = extractPublicId(publicIdOrUrl);
            
            Map<?, ?> result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            return "ok".equals(result.get("result"));
        } catch (Exception e) {
            System.err.println("Error deleting file from Cloudinary: " + e.getMessage());
            return false;
        }
    }

    /**
     * Extract public ID from Cloudinary URL
     */
    private String extractPublicId(String url) {
        if (url == null || url.isEmpty()) {
            return url;
        }
        
        // Cloudinary URL format: https://res.cloudinary.com/{cloud_name}/raw/upload/{version}/{public_id}
        // Extract public_id from URL
        try {
            String[] parts = url.split("/");
            String publicIdWithExt = parts[parts.length - 1];
            // Remove file extension if present
            return publicIdWithExt.substring(0, publicIdWithExt.lastIndexOf('.'));
        } catch (Exception e) {
            // If extraction fails, return as is (might be a public ID already)
            return url;
        }
    }
}

