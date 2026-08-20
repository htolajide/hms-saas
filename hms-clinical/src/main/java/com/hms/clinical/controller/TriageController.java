package com.hms.clinical.controller;

import com.hms.clinical.dto.TriageRequestDto;
import com.hms.clinical.dto.TriageResponseDto;
import com.hms.clinical.service.TriageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/triage")
@RequiredArgsConstructor
public class TriageController {

    private final TriageService triageService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_SUPER_ADMIN', 'ROLE_HOSPITAL_ADMIN', 'ROLE_NURSE')")
    public ResponseEntity<TriageResponseDto> createTriage(@RequestBody TriageRequestDto dto) {
        TriageResponseDto created = triageService.createTriage(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasAnyAuthority('ROLE_SUPER_ADMIN', 'ROLE_HOSPITAL_ADMIN', 'ROLE_DOCTOR', 'ROLE_NURSE')")
    public ResponseEntity<List<TriageResponseDto>> getPatientTriageHistory(@PathVariable Long patientId) {
        return ResponseEntity.ok(triageService.getTriageByPatient(patientId));
    }
}