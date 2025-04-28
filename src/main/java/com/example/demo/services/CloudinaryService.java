package com.example.demo.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public String uploadPdf(MultipartFile file) throws IOException {
        if (file == null) {
            throw new IllegalArgumentException("File cannot be null");
        }
        
        byte[] content = file.getBytes();
        if (content.length == 0) {
            throw new IllegalArgumentException("File cannot be empty");
        }

        Map<String, Object> uploadResult = cloudinary.uploader().upload(content,
                ObjectUtils.asMap(
                        "resource_type", "raw",  // "raw" is needed for PDFs
                        "folder", "pdf-uploads"
                ));

        if (uploadResult == null || uploadResult.get("secure_url") == null) {
            throw new RuntimeException("Failed to get secure URL from upload response");
        }

        return uploadResult.get("secure_url").toString();
    }
}