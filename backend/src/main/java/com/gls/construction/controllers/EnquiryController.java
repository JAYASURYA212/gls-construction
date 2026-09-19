package com.gls.construction.controllers;

import com.gls.construction.models.Enquiry;
import com.gls.construction.repositories.EnquiryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import java.util.Map;

import java.util.List;

@RestController
@RequestMapping("/api/enquiries")
public class EnquiryController {

    @Autowired
    private EnquiryRepository enquiryRepository;

    @GetMapping
    public ResponseEntity<?> getAllEnquiries(HttpSession session) {
        if (session.getAttribute("adminUser") == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Admin login required"));
        }
        return ResponseEntity.ok(enquiryRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<String> submitEnquiry(@RequestBody Enquiry enquiry) {
        try {
            // Save the enquiry to the database
            enquiryRepository.save(enquiry);
            return ResponseEntity.ok("Enquiry submitted successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error submitting enquiry. Please try again.");
        }
    }
}