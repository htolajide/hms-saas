package com.hms.api.service;

import com.hms.api.dto.HospitalRegistrationRequestDto;
import com.hms.core.entity.Hospital;
import com.hms.core.repository.HospitalRepository;
import com.hms.staff.entity.Role;
import com.hms.staff.entity.Staff;
import com.hms.staff.repository.RoleRepository;
import com.hms.staff.repository.StaffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class HospitalRegistrationService {

    private final HospitalRepository hospitalRepository;
    private final StaffRepository staffRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void registerHospital(HospitalRegistrationRequestDto dto) {
        // 1. Check if hospital code or admin email already exists
        if (hospitalRepository.existsByHospitalCode(dto.getHospitalCode())) {
            throw new RuntimeException("Hospital code already exists: " + dto.getHospitalCode());
        }
        if (staffRepository.existsByEmail(dto.getAdminEmail())) {
            throw new RuntimeException("Admin email is already registered: " + dto.getAdminEmail());
        }

        // 2. Create the Hospital
        Hospital hospital = Hospital.builder()
                .hospitalCode(dto.getHospitalCode())
                .name(dto.getHospitalName())
                .address(dto.getAddress())
                .phone(dto.getPhone())
                .isActive(true)
                .build();

        Hospital savedHospital = hospitalRepository.save(hospital);

        // 3. Find the "Hospital Admin" role
        Role adminRole = roleRepository.findByName("Hospital Admin")
                .orElseThrow(() -> new RuntimeException(
                        "System Error: Hospital Admin role not found. Please contact support."));

        // 4. Create the Initial Admin User
        Staff admin = Staff.builder()
                .hospitalId(savedHospital.getId())
                .staffId("ADMIN-001") // Default staff ID for the owner
                .fullName(dto.getAdminFullName())
                .email(dto.getAdminEmail())
                .password(passwordEncoder.encode(dto.getAdminPassword()))
                .role(adminRole)
                .joinedDate(LocalDate.now())
                .build();

        staffRepository.save(admin);
    }
}