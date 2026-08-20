package com.hms.clinical.controller;

import com.hms.clinical.dto.PatientRequestDto;
import com.hms.clinical.dto.PatientResponseDto;
import com.hms.clinical.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_SUPER_ADMIN', 'ROLE_HOSPITAL_ADMIN', 'ROLE_DOCTOR', 'ROLE_NURSE')")
    public ResponseEntity<List<PatientResponseDto>> getAllPatients(@RequestParam Long hospitalId) {
        return ResponseEntity.ok(patientService.getAllPatients(hospitalId));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_SUPER_ADMIN', 'ROLE_HOSPITAL_ADMIN', 'ROLE_NURSE')")
    public ResponseEntity<PatientResponseDto> createPatient(@RequestBody PatientRequestDto dto) {
        PatientResponseDto created = patientService.createPatient(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }
}