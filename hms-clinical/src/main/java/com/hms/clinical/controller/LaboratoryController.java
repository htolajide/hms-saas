package com.hms.clinical.controller;

import com.hms.clinical.dto.LabTestRequestDto; // Create this DTO if not existing
import com.hms.clinical.dto.LabTestResponseDto;
import com.hms.clinical.entity.LabOrder;
import com.hms.clinical.entity.LabTest;
import com.hms.clinical.service.LabTestService;
import com.hms.core.security.HospitalAuthenticationToken;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/laboratory")
@RequiredArgsConstructor
public class LaboratoryController {
    private final LabTestService labService;

    @GetMapping("/tests")
    @PreAuthorize("hasAnyAuthority('ROLE_SUPER_ADMIN', 'ROLE_HOSPITAL_ADMIN', 'ROLE_LAB_TECHNOLOGIST', 'ROLE_DOCTOR')")
    public ResponseEntity<List<LabTestResponseDto>> getTests() {
        HospitalAuthenticationToken auth = (HospitalAuthenticationToken) SecurityContextHolder.getContext()
                .getAuthentication();

        Long hospitalId = auth.getHospitalId();
        if (hospitalId == null) {
            throw new AccessDeniedException("Super Admin cannot access hospital-specific lab tests");
        }
        return ResponseEntity.ok(labService.getTestsByHospital(hospitalId));
    }

    @PostMapping("/tests")
    @PreAuthorize("hasAnyAuthority('ROLE_SUPER_ADMIN', 'ROLE_HOSPITAL_ADMIN', 'ROLE_LAB_TECHNOLOGIST')")
    public ResponseEntity<LabTest> saveTest(@RequestBody LabTestRequestDto dto) {
        HospitalAuthenticationToken auth = (HospitalAuthenticationToken) SecurityContextHolder.getContext()
                .getAuthentication();

        // Force hospitalId from token, ignore any value sent from frontend
        dto.setHospitalId(auth.getHospitalId());

        return ResponseEntity.ok(labService.createOrUpdateTest(dto));
    }

    @GetMapping("/orders/pending")
    @PreAuthorize("hasAnyAuthority('ROLE_SUPER_ADMIN', 'ROLE_HOSPITAL_ADMIN', 'ROLE_LAB_TECHNOLOGIST')")
    public ResponseEntity<List<LabOrder>> getPendingOrders() {
        HospitalAuthenticationToken auth = (HospitalAuthenticationToken) SecurityContextHolder.getContext()
                .getAuthentication();

        Long hospitalId = auth.getHospitalId();
        if (hospitalId == null) {
            throw new AccessDeniedException("Super Admin cannot access pending orders");
        }
        return ResponseEntity.ok(labService.getPendingOrders(hospitalId));
    }

    @PostMapping("/orders/{id}/result")
    @PreAuthorize("hasAnyAuthority('ROLE_LAB_TECHNOLOGIST')")
    public ResponseEntity<LabOrder> postResult(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(labService.postLabResult(id, body.get("result")));
    }
}