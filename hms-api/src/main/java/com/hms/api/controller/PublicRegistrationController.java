package com.hms.api.controller;

import com.hms.api.dto.HospitalRegistrationRequestDto;
import com.hms.api.service.HospitalRegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/public")
@RequiredArgsConstructor
public class PublicRegistrationController {

    private final HospitalRegistrationService registrationService;

    @PostMapping("/register-hospital")
    public ResponseEntity<?> registerHospital(@RequestBody HospitalRegistrationRequestDto dto) {
        try {
            registrationService.registerHospital(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(
                    Map.of("message", "Hospital and Admin account created successfully! Please login."));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}