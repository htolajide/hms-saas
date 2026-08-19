package com.hms.core.controller;

import com.hms.core.dto.HospitalRequestDto;
import com.hms.core.entity.Hospital;
import com.hms.core.repository.HospitalRepository;
import com.hms.core.service.HospitalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/hospitals")
@RequiredArgsConstructor
public class HospitalController {

    private final HospitalService hospitalService;
    private final HospitalRepository hospitalRepository;

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')") // ONLY Super Admin can create hospitals
    public ResponseEntity<Hospital> createHospital(@RequestBody HospitalRequestDto dto) {
        Hospital created = hospitalService.createHospital(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_SUPER_ADMIN', 'ROLE_HOSPITAL_ADMIN')")
    public ResponseEntity<List<Hospital>> getAllHospitals() {
        return ResponseEntity.ok(hospitalRepository.findAll()); // Note: inject HospitalRepository here
    }
}