package com.example.demo.controllers;

import com.example.demo.services.CloudinaryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("api/upload")
//@CrossOrigin(origins = "http://localhost:5173/")
public class FileUploadController {

    private final CloudinaryService cloudinaryService;

    @Autowired
    public FileUploadController(CloudinaryService cloudinaryService) {
        this.cloudinaryService = cloudinaryService;
    }

    @PostMapping(value ="/pdf", consumes = "multipart/form-data")
    public ResponseEntity<String> uploadPdf(@RequestParam("file") MultipartFile file) {
        log.info("Received a request to upload a PDF.");

        if (file == null || file.isEmpty()) {
            log.error("No file provided or file is empty!");
            return ResponseEntity.badRequest().body("No file uploaded!");
        }

        try {
            log.info("Uploading file: {}", file.getOriginalFilename());
            String url = cloudinaryService.uploadPdf(file);
            log.info("File uploaded successfully: {}", url);
            return ResponseEntity.ok(url);
        } catch (IOException e) {
            log.error("Upload failed: {}", e.getMessage());
            return ResponseEntity.status(500).body("Upload failed: " + e.getMessage());
        }
    }
}