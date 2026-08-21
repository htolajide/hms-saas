package com.hms.core.controller;

import com.hms.core.entity.Hospital;
import com.hms.core.repository.HospitalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final HospitalRepository hospitalRepository;

    @GetMapping("/hospitals")
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
    public ResponseEntity<List<Hospital>> getAllHospitals() {
        return ResponseEntity.ok(hospitalRepository.findAll());
    }

    @PatchMapping("/hospitals/{id}/status")
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
    public ResponseEntity<?> toggleHospitalStatus(@PathVariable Long id, @RequestBody Map<String, Boolean> body) {
        Hospital hospital = hospitalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hospital not found"));
        hospital.setIsActive(body.get("isActive"));
        hospitalRepository.save(hospital);
        return ResponseEntity.ok(Map.of("message", "Hospital status updated"));
    }
}