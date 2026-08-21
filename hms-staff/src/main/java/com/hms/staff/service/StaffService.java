package com.hms.staff.service;

import com.hms.core.security.HospitalAuthenticationToken;
import com.hms.staff.dto.StaffRequestDto;
import com.hms.staff.dto.StaffResponseDto;
import com.hms.staff.entity.Department;
import com.hms.staff.entity.Rank;
import com.hms.staff.entity.Role;
import com.hms.staff.entity.Staff;
import com.hms.staff.repository.DepartmentRepository;
import com.hms.staff.repository.RankRepository;
import com.hms.staff.repository.RoleRepository;
import com.hms.staff.repository.StaffRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StaffService {

    private final StaffRepository staffRepository;
    private final RoleRepository roleRepository;
    private final RankRepository rankRepository;
    private final PasswordEncoder passwordEncoder;
    private final DepartmentRepository departmentRepository;

    // Upload directory
    private final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads/staff-photos/";

    @Transactional(readOnly = true)
    public List<StaffResponseDto> getAllStaff() {
        // In StaffService.createStaff(), getAllStaff(), etc.
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        Long hospitalId;
        boolean isSuperAdmin = false;

        if (auth instanceof HospitalAuthenticationToken) {
            HospitalAuthenticationToken hat = (HospitalAuthenticationToken) auth;
            hospitalId = hat.getHospitalId();
            isSuperAdmin = (hat.getHospitalId() == null);
        } else {
            // Fallback: This should NEVER happen in production, but prevents crashes
            throw new AccessDeniedException("Invalid authentication context. Please re-login.");
        }

        String role = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst().orElse("");

        List<Staff> staffList;

        // Super Admin can see ALL staff across all hospitals
        // Super Admin CANNOT access hospital-specific endpoints
        if (isSuperAdmin) {
            staffList = staffRepository.findByHospitalId(null);
        }
        // Hospital Admin can ONLY see staff in their specific hospital
        else {
            staffList = staffRepository.findByHospitalId(hospitalId);
        }

        return staffList.stream()
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
    public StaffResponseDto createStaff(StaffRequestDto dto, MultipartFile photo) throws IOException {
        if (dto.getRoleId() == null) {
            throw new RuntimeException("Role ID is required");
        }
        Role role = roleRepository.findById(dto.getRoleId())
                .orElseThrow(() -> new RuntimeException("Role not found with ID: " + dto.getRoleId()));

        // Validate Rank
        if (dto.getRankId() == null) {
            throw new RuntimeException("Rank ID is required. Please select a rank/designation.");
        }
        Rank rank = rankRepository.findById(dto.getRankId())
                .orElseThrow(() -> new RuntimeException("Rank not found with ID: " + dto.getRankId()));

        // Check if email already exists
        if (staffRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email already in use: " + dto.getEmail());
        }

        if (dto.getDepartmentId() == null) {
            throw new RuntimeException("Department ID is required");
        }
        Department department = departmentRepository.findById(dto.getDepartmentId())
                .orElseThrow(() -> new RuntimeException("Department not found with ID: " + dto.getDepartmentId()));

        String photoFilename = null;
        if (photo != null && !photo.isEmpty()) {
            photoFilename = savePhoto(photo);
        }
        // In StaffService.createStaff(), getAllStaff(), etc.
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        Long hospitalId;
        boolean isSuperAdmin = false;

        if (auth instanceof HospitalAuthenticationToken) {
            HospitalAuthenticationToken hat = (HospitalAuthenticationToken) auth;
            hospitalId = hat.getHospitalId();
            isSuperAdmin = (hat.getHospitalId() == null);
        } else {
            // Fallback: This should NEVER happen in production, but prevents crashes
            throw new AccessDeniedException("Invalid authentication context. Please re-login.");
        }

        // Now use hospitalId safely

        Staff staff = Staff.builder()
                .hospitalId(hospitalId)
                .staffId(dto.getStaffId())
                .fullName(dto.getFullName())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .role(role)
                .rank(rank) // NEW: Link the rank
                .department(department)
                .phone(dto.getPhone())
                .passportPhoto(photoFilename)
                .qualification(dto.getQualification())
                .build();

        return mapToResponseDto(staffRepository.save(staff));
    }

    private String savePhoto(MultipartFile file) throws IOException {
        // Create upload directory if it doesn't exist
        Path uploadPath = Paths.get(UPLOAD_DIR);
        System.out.println("UPLOAD DIRECTORY: " + uploadPath.toAbsolutePath()); // ADD THIS
        System.out.println("Directory exists: " + Files.exists(uploadPath)); // ADD THIS
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String filename = UUID.randomUUID().toString() + extension;

        // Save file
        Path filePath = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath);

        return filename;
    }

    @Transactional
    public StaffResponseDto updateStaff(Long id, StaffRequestDto dto, MultipartFile photo) {
        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Staff member not found with ID: " + id));

        Role role = roleRepository.findById(dto.getRoleId())
                .orElseThrow(() -> new RuntimeException("Role not found with ID: " + dto.getRoleId()));

        // 1. Fetch the new Rank
        Rank rank = rankRepository.findById(dto.getRankId())
                .orElseThrow(() -> new RuntimeException("Rank not found with ID: " + dto.getRankId()));
        if (dto.getDepartmentId() == null) {
            throw new RuntimeException("Department ID is required");
        }
        Department department = departmentRepository.findById(dto.getDepartmentId())
                .orElseThrow(() -> new RuntimeException("Department not found with ID: " + dto.getDepartmentId()));

        // 2. Handle photo replacement
        if (photo != null && !photo.isEmpty()) {
            if (staff.getPassportPhoto() != null) {
                try {
                    Files.deleteIfExists(Paths.get(UPLOAD_DIR + staff.getPassportPhoto()));
                } catch (IOException ignored) {
                }
            }
            try {
                staff.setPassportPhoto(savePhoto(photo));
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload photo: " + e.getMessage());
            }
        }

        // 3. Update basic fields
        staff.setStaffId(dto.getStaffId());
        staff.setFullName(dto.getFullName());
        staff.setEmail(dto.getEmail());

        // Only update password if a new one was provided
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            staff.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        // 4. Update Role, Rank, and Department
        staff.setRole(role);
        staff.setRank(rank); // <-- NEW: Sets the rank (which contains the designation & salary)
        staff.setDepartment(department);
        staff.setPhone(dto.getPhone());
        staff.setQualification(dto.getQualification());

        return mapToResponseDto(staffRepository.save(staff));
    }

    // Helper method to map Entity to Response DTO
    private StaffResponseDto mapToResponseDto(Staff staff) {
        return StaffResponseDto.builder()
                .id(staff.getId())
                .staffId(staff.getStaffId())
                .roleId(staff.getRole() != null ? staff.getRole().getId() : 0)
                .rankId(staff.getRank() != null ? staff.getRank().getId() : 0)
                .fullName(staff.getFullName())
                .email(staff.getEmail())
                .roleName(staff.getRole() != null ? staff.getRole().getName() : "No Role")
                .rankName(staff.getRank() != null ? staff.getRank().getName() : "No Rank") // <-- NEW
                .basicSalary(staff.getRank() != null ? staff.getRank().getBasicSalary() : java.math.BigDecimal.ZERO) // <--
                                                                                                                     // //
                                                                                                                     // NEW
                .departmentName(staff.getDepartment() != null ? staff.getDepartment().getName() : "No Department")
                .departmentId(staff.getDepartment() != null ? staff.getDepartment().getId() : 0)
                .phone(staff.getPhone())
                .passportPhoto(staff.getPassportPhoto())
                .qualification(staff.getQualification())
                .build();
    }

    @Transactional
    public void deleteStaff(Long id) {
        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Staff member not found with ID: " + id));

        if (staff.getPassportPhoto() != null) {
            try {
                Files.deleteIfExists(Paths.get(UPLOAD_DIR + staff.getPassportPhoto()));
            } catch (IOException ignored) {
            }
        }
        staffRepository.delete(staff);
    }
}