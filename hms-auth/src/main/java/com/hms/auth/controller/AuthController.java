package com.hms.auth.controller;

import com.hms.auth.service.CustomUserDetailsService;
import com.hms.auth.service.JwtService;
import com.hms.auth.service.PasswordResetService;
import com.hms.staff.entity.Staff;
import com.hms.staff.repository.StaffRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;
    private final StaffRepository staffRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetService passwordResetService; // Add this at the top of the class

    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService,
            CustomUserDetailsService userDetailsService, StaffRepository staffRepository,
            PasswordResetService passwordResetService,
            PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.staffRepository = staffRepository;
        this.passwordEncoder = passwordEncoder;
        this.passwordResetService = passwordResetService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(credentials.get("email"), credentials.get("password")));

            Staff staff = (Staff) authentication.getPrincipal();
            String token = jwtService.generateToken(staff.getEmail(), staff.getRole().getName());

            return ResponseEntity.ok(Map.of(
                    "token", token,
                    "message", "Login successful",
                    "role", staff.getRole().getName(),
                    "fullName", staff.getFullName()));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid email or password"));
        }
    }

    @GetMapping("/test-secure")
    public ResponseEntity<?> testSecureEndpoint() {
        return ResponseEntity
                .ok(Map.of("message", "🔒 Success! You are authenticated and accessing a secure endpoint."));
    }

    @PostMapping("/super-admin/reset-password")
    public ResponseEntity<?> adminResetPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String newPassword = request.get("newPassword");

        return staffRepository.findByEmail(email).map(staff -> {
            staff.setPassword(passwordEncoder.encode(newPassword));
            staffRepository.save(staff);
            return ResponseEntity.ok(Map.of("message", "Password reset successfully for " + email));
        }).orElseGet(() -> ResponseEntity.status(404).body(Map.of("error", "Staff member not found")));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
        try {
            passwordResetService.generateResetToken(request.get("email"));
            return ResponseEntity
                    .ok(Map.of("message", "If an account exists with that email, a reset link has been sent."));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        try {
            passwordResetService.resetPassword(request.get("token"), request.get("newPassword"));
            return ResponseEntity.ok(Map.of("message", "Password updated successfully. Please login."));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}