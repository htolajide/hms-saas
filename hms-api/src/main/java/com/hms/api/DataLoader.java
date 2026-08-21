package com.hms.api;

import com.hms.staff.entity.Role;
import com.hms.staff.entity.Staff;
import com.hms.staff.repository.RoleRepository;
import com.hms.staff.repository.StaffRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

@Configuration
public class DataLoader {

    @Bean
    public CommandLineRunner initData(StaffRepository staffRepository, RoleRepository roleRepository,
            PasswordEncoder passwordEncoder) {
        return args -> {

            // Ensure this logic exists and runs:
            if (!staffRepository.existsByEmail("superadmin@hms-saas.com")) {
                Role adminRole = roleRepository.findByName("Super Admin")
                        .orElseThrow(() -> new RuntimeException("Super Admin role missing!"));

                Staff superAdmin = Staff.builder()
                        .hospitalId(null) // ✅ EXPLICITLY NULL
                        .staffId("SA-0001")
                        .fullName("SaaS Super Administrator")
                        .email("superadmin@hms-saas.com")
                        .password(passwordEncoder.encode("admin123"))
                        .role(adminRole)
                        .joinedDate(LocalDate.now())
                        .build();

                staffRepository.save(superAdmin);
                System.out.println("✅ SUPER ADMIN CREATED (No Hospital ID)");
            }

            System.out.println("===========================================================");
            System.out.println("🚀 Database seeded successfully!");
            System.out.println("===========================================================");
        };
    }
}