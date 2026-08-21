package com.hms.staff.controller;

import com.hms.staff.dto.StaffRequestDto;
import com.hms.staff.dto.StaffResponseDto;
import com.hms.staff.service.StaffService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/staff")
@RequiredArgsConstructor
public class StaffController {

    private final StaffService staffService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_SUPER_ADMIN', 'ROLE_HOSPITAL_ADMIN')")
    public ResponseEntity<List<StaffResponseDto>> getAllStaff() {
        // Service handles the scoping automatically based on JWT
        return ResponseEntity.ok(staffService.getAllStaff());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StaffResponseDto> getStaffById(@PathVariable Long id) {
        return ResponseEntity.ok(staffService.getStaffById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN') or hasAuthority('ROLE_HOSPITAL_ADMIN')")
    public ResponseEntity<StaffResponseDto> createStaff(
            @RequestPart("staff") StaffRequestDto dto,
            @RequestPart(value = "photo", required = false) MultipartFile photo) throws IOException {

        StaffResponseDto createdStaff = staffService.createStaff(dto, photo);
        return new ResponseEntity<>(createdStaff, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN') or hasAuthority('ROLE_HOSPITAL_ADMIN')")
    public ResponseEntity<StaffResponseDto> updateStaff(
            @PathVariable("id") Long id,
            @RequestPart("staff") StaffRequestDto dto,
            @RequestPart(value = "photo", required = false) MultipartFile photo) {

        // --- DEBUG LOGGING ---
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("==================================================");
        System.out.println("DEBUG: Current User = " + auth.getName());
        System.out.println("DEBUG: Authorities = " + auth.getAuthorities());
        System.out.println("==================================================");
        // -----------------------

        StaffResponseDto updatedStaff = staffService.updateStaff(id, dto, photo);
        return ResponseEntity.ok(updatedStaff);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN') or hasAuthority('ROLE_HOSPITAL_ADMIN')")
    public ResponseEntity<?> deleteStaff(@PathVariable Long id) {
        staffService.deleteStaff(id);
        return ResponseEntity.ok(Map.of("message", "Staff member deleted successfully"));
    }
}