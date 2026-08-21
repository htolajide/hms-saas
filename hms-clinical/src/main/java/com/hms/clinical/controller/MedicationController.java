package com.hms.clinical.controller;

import com.hms.clinical.dto.MedicationMasterResponseDto;
import com.hms.clinical.dto.MedicationStockRequestDto; // Create this DTO
import com.hms.clinical.entity.Medication;
import com.hms.clinical.entity.MedicationMaster;
import com.hms.clinical.service.MedicationService;
import com.hms.core.security.HospitalAuthenticationToken;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pharmacy")
@RequiredArgsConstructor
public class MedicationController {
    private final MedicationService medService;

    @GetMapping("/medications")
    @PreAuthorize("hasAnyAuthority('ROLE_SUPER_ADMIN', 'ROLE_HOSPITAL_ADMIN', 'ROLE_PHARMACIST', 'ROLE_DOCTOR')")
    public ResponseEntity<List<Medication>> getMedications() {
        HospitalAuthenticationToken auth = (HospitalAuthenticationToken) SecurityContextHolder.getContext()
                .getAuthentication();

        Long hospitalId = auth.getHospitalId();
        if (hospitalId == null) {
            throw new AccessDeniedException("Super Admin cannot access hospital inventory");
        }
        return ResponseEntity.ok(medService.getMedicationsByHospital(hospitalId));
    }

    @PostMapping("/medications")
    @PreAuthorize("hasAnyAuthority('ROLE_SUPER_ADMIN', 'ROLE_HOSPITAL_ADMIN', 'ROLE_PHARMACIST')")
    public ResponseEntity<Medication> saveMedication(@RequestBody MedicationStockRequestDto dto) {
        HospitalAuthenticationToken auth = (HospitalAuthenticationToken) SecurityContextHolder.getContext()
                .getAuthentication();

        dto.setHospitalId(auth.getHospitalId());
        return ResponseEntity.ok(medService.addStockToMaster(dto));
    }

    @DeleteMapping("/medications/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_SUPER_ADMIN', 'ROLE_HOSPITAL_ADMIN', 'ROLE_PHARMACIST')")
    public ResponseEntity<?> deleteMedication(@PathVariable Long id) {
        medService.deleteMedication(id);
        return ResponseEntity.ok("Deleted");
    }

    @GetMapping("/masters")
    public ResponseEntity<List<MedicationMasterResponseDto>> getMasterCatalog() {
        HospitalAuthenticationToken auth = (HospitalAuthenticationToken) SecurityContextHolder.getContext()
                .getAuthentication();
        return ResponseEntity.ok(medService.getMasterCatalog(auth.getHospitalId()));
    }

    @PostMapping("/masters")
    @PreAuthorize("hasAnyAuthority('ROLE_SUPER_ADMIN', 'ROLE_HOSPITAL_ADMIN', 'ROLE_PHARMACIST')")
    public ResponseEntity<MedicationMaster> createMaster(@RequestBody MedicationMaster master) {
        HospitalAuthenticationToken auth = (HospitalAuthenticationToken) SecurityContextHolder.getContext()
                .getAuthentication();

        master.setHospitalId(auth.getHospitalId());
        return ResponseEntity.ok(medService.createMaster(master));
    }
}