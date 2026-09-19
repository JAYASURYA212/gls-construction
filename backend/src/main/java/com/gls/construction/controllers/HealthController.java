package com.gls.construction.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import java.util.Map;

@RestController
public class HealthController {
    
    @GetMapping("/")
    public RedirectView home() {
        return new RedirectView("/index.html");
    }

    @GetMapping("/api")
    public ResponseEntity<?> apiIndex() {
        return ResponseEntity.ok(Map.of(
            "status", "healthy",
            "message", "GLS Construction API",
            "health", "/api/health",
            "projects", "/api/projects",
            "enquiries", "/api/enquiries",
            "adminLogin", "/api/auth/login"
        ));
    }
    
    @GetMapping("/api/health")
    public ResponseEntity<?> apiHealth() {
        return ResponseEntity.ok(Map.of(
            "status", "healthy",
            "endpoints", new String[]{
                "/api/projects",
                "/api/projects/{id}",
                "/api/enquiries",
                "/api/auth/login"
            }
        ));
    }
}
