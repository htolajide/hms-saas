package com.hms.staff.service;

import com.hms.staff.dto.StaffRequestDto;
import com.hms.staff.dto.StaffResponseDto;
import com.hms.staff.entity.Role;
import com.hms.staff.entity.Staff;
import com.hms.staff.repository.RoleRepository;
import com.hms.staff.repository.StaffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StaffService {

    private final StaffRepository staffRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<StaffResponseDto> getAllStaff() {
        return staffRepository.findAll().stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public StaffResponseDto getStaffById(Long id) {
        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Staff member not found with ID: " + id));
        return mapToResponseDto(staff);
    }

    @Transactional
    public StaffResponseDto createStaff(StaffRequestDto dto) {
        // 1. Find the Role
        Role role = roleRepository.findById(dto.getRoleId())
                .orElseThrow(() -> new RuntimeException("Role not found with ID: " + dto.getRoleId()));

        // 2. Check if email already exists
        if (staffRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email already in use: " + dto.getEmail());
        }

        // 3. Map DTO to Entity
        Staff staff = Staff.builder()
                .hospitalId(dto.getHospitalId())
                .staffId(dto.getStaffId())
                .fullName(dto.getFullName())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword())) // Hash the password!
                .role(role)
                .department(dto.getDepartment())
                .designation(dto.getDesignation())
                .basicSalary(dto.getBasicSalary())
                .phone(dto.getPhone())
                .build();

        Staff savedStaff = staffRepository.save(staff);
        return mapToResponseDto(savedStaff);
    }

    // Helper method to map Entity to Response DTO
    private StaffResponseDto mapToResponseDto(Staff staff) {
        return StaffResponseDto.builder()
                .id(staff.getId())
                .staffId(staff.getStaffId())
                .fullName(staff.getFullName())
                .email(staff.getEmail())
                .roleName(staff.getRole() != null ? staff.getRole().getName() : "No Role")
                .department(staff.getDepartment())
                .designation(staff.getDesignation())
                .basicSalary(staff.getBasicSalary())
                .phone(staff.getPhone())
                .build();
    }
}