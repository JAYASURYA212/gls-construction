package com.gls.construction.controllers;

import com.gls.construction.models.AdminUser;
import com.gls.construction.repositories.AdminUserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController @RequestMapping("/api/auth")
public class AuthController {
    private final AdminUserRepository users;
    private final PasswordEncoder encoder;
    public AuthController(AdminUserRepository users, PasswordEncoder encoder) { this.users = users; this.encoder = encoder; }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body, HttpSession session) {
        var user = users.findByUsername(body.getOrDefault("username", ""));
        if (user.isEmpty() || !encoder.matches(body.getOrDefault("password", ""), user.get().getPasswordHash())) return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
        session.setAttribute("adminUser", user.get().getUsername());
        return ResponseEntity.ok(Map.of("username", user.get().getUsername()));
    }
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody Map<String, String> body, HttpSession session) {
        String username = body.getOrDefault("username", "").trim();
        String password = body.getOrDefault("password", "");
        if (username.isBlank() || password.length() < 8) {
            return ResponseEntity.badRequest().body(Map.of("error", "Username is required and password must be at least 8 characters"));
        }
        if (users.findByUsername(username).isPresent()) {
            return ResponseEntity.status(409).body(Map.of("error", "Username already exists"));
        }
        AdminUser user = new AdminUser();
        user.setUsername(username);
        user.setPasswordHash(encoder.encode(password));
        users.save(user);
        session.setAttribute("adminUser", username);
        return ResponseEntity.ok(Map.of("username", username));
    }
    @PostMapping("/logout") public ResponseEntity<Void> logout(HttpSession session) { session.invalidate(); return ResponseEntity.noContent().build(); }
    @GetMapping("/me") public ResponseEntity<?> me(HttpSession session) { Object user = session.getAttribute("adminUser"); return user == null ? ResponseEntity.status(401).build() : ResponseEntity.ok(Map.of("username", user)); }
    @GetMapping("/csrf") public Map<String, String> csrf(CsrfToken token) { return Map.of("token", token.getToken()); }
}