package com.hms.auth.controller;

import com.hms.auth.service.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(JwtService jwtService, PasswordEncoder passwordEncoder) {
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        // TODO: Add actual user lookup logic in Stage 2
        String token = jwtService.generateToken(credentials.get("email"), "ROLE_ADMIN");
        return ResponseEntity.ok(Map.of("token", token, "message", "Login successful"));
    }

    @PostMapping("/super-admin/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        // TODO: Add logic to find user by email and update password
        String hashedPassword = passwordEncoder.encode(request.get("newPassword"));
        return ResponseEntity.ok(Map.of("message", "Password reset successfully (Mock)", "hashed", hashedPassword));
    }
}