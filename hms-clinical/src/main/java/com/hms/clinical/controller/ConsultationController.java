package com.hms.clinical.controller;

import com.hms.clinical.dto.ConsultationRequestDto;
import com.hms.clinical.dto.ConsultationResponseDto;
import com.hms.clinical.service.ConsultationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/consultations")
@RequiredArgsConstructor
public class ConsultationController {

    private final ConsultationService consultationService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_SUPER_ADMIN', 'ROLE_HOSPITAL_ADMIN', 'ROLE_DOCTOR')")
    public ResponseEntity<ConsultationResponseDto> createConsultation(@RequestBody ConsultationRequestDto dto) {
        ConsultationResponseDto created = consultationService.createConsultation(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_SUPER_ADMIN', 'ROLE_HOSPITAL_ADMIN', 'ROLE_DOCTOR', 'ROLE_NURSE')")
    public ResponseEntity<ConsultationResponseDto> getConsultation(@PathVariable Long id) {
        return ResponseEntity.ok(consultationService.getConsultationById(id));
    }

    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasAnyAuthority('ROLE_SUPER_ADMIN', 'ROLE_HOSPITAL_ADMIN', 'ROLE_DOCTOR', 'ROLE_NURSE')")
    public ResponseEntity<List<ConsultationResponseDto>> getPatientConsultations(@PathVariable Long patientId) {
        // Use the service method we just added!
        return ResponseEntity.ok(consultationService.getConsultationsByPatientId(patientId));
    }
}