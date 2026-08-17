package com.hms.staff.controller;

import com.hms.staff.dto.StaffRequestDto;
import com.hms.staff.dto.StaffResponseDto;
import com.hms.staff.service.StaffService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/staff")
@RequiredArgsConstructor
public class StaffController {

    private final StaffService staffService;

    // Anyone with a valid token can view staff
    @GetMapping
    public ResponseEntity<List<StaffResponseDto>> getAllStaff() {
        return ResponseEntity.ok(staffService.getAllStaff());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StaffResponseDto> getStaffById(@PathVariable Long id) {
        return ResponseEntity.ok(staffService.getStaffById(id));
    }

    // ONLY Super Admin or Hospital Admin can create staff
    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'HOSPITAL_ADMIN')")
    public ResponseEntity<StaffResponseDto> createStaff(@RequestBody StaffRequestDto dto) {
        StaffResponseDto createdStaff = staffService.createStaff(dto);
        return new ResponseEntity<>(createdStaff, HttpStatus.CREATED);
    }
}