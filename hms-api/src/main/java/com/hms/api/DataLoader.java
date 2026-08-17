package com.hms.api;

import com.hms.staff.entity.Role;
import com.hms.staff.entity.Staff;
import com.hms.staff.repository.RoleRepository;
import com.hms.staff.repository.StaffRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Configuration
public class DataLoader {

    @Bean
    public CommandLineRunner initData(StaffRepository staffRepository, RoleRepository roleRepository,
            PasswordEncoder passwordEncoder) {
        return args -> {

            // 1. Create Global Super Admin Role (hospitalId = null)
            Role superAdminRole = roleRepository.findByName("Super Admin").orElseGet(() -> {
                Role role = new Role();
                role.setName("Super Admin");
                role.setDescription("SaaS Owner - Full system access across all hospitals");
                role.setHospitalId(null);
                return roleRepository.save(role);
            });

            // 2. Create Hospital Admin Role (hospitalId = 1)
            Role hospitalAdminRole = roleRepository.findByName("Hospital Admin").orElseGet(() -> {
                Role role = new Role();
                role.setName("Hospital Admin");
                role.setDescription("Manages settings and staff for a specific hospital");
                role.setHospitalId(1L);
                return roleRepository.save(role);
            });

            // 3. Create Doctor Role (hospitalId = 1)
            Role doctorRole = roleRepository.findByName("Doctor").orElseGet(() -> {
                Role role = new Role();
                role.setName("Doctor");
                role.setDescription("Handles consultations and prescriptions");
                role.setHospitalId(1L);
                return roleRepository.save(role);
            });

            // 4. Create the SaaS Super Admin User
            if (!staffRepository.existsByEmail("superadmin@hms-saas.com")) {
                Staff superAdmin = Staff.builder()
                        .hospitalId(null) // Belongs to the SaaS platform, not a specific hospital
                        .staffId("SA-0001")
                        .fullName("SaaS Super Administrator")
                        .email("superadmin@hms-saas.com")
                        .password(passwordEncoder.encode("super123"))
                        .role(superAdminRole)
                        .department("Executive")
                        .designation("Super Admin")
                        .basicSalary(new BigDecimal("0.00"))
                        .phone("0000000000")
                        .joinedDate(LocalDate.now())
                        .build();
                staffRepository.save(superAdmin);
                System.out.println("✅ SUPER ADMIN CREATED: superadmin@hms-saas.com / super123");
            }

            // 5. Create the Hospital Admin User
            if (!staffRepository.existsByEmail("admin@firstmercy.com")) {
                Staff hospitalAdmin = Staff.builder()
                        .hospitalId(1L) // Belongs to Hospital 1
                        .staffId("STF-0001")
                        .fullName("Hospital Administrator")
                        .email("admin@firstmercy.com")
                        .password(passwordEncoder.encode("admin123"))
                        .role(hospitalAdminRole)
                        .department("Administration")
                        .designation("Hospital Admin")
                        .basicSalary(new BigDecimal("0.00"))
                        .phone("08000000000")
                        .joinedDate(LocalDate.now())
                        .build();
                staffRepository.save(hospitalAdmin);
                System.out.println("✅ HOSPITAL ADMIN CREATED: admin@firstmercy.com / admin123");
            }

            System.out.println("===========================================================");
            System.out.println("🚀 Database seeded successfully!");
            System.out.println("===========================================================");
        };
    }
}