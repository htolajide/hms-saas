package com.hms.api;

import com.hms.staff.entity.Department;
import com.hms.staff.entity.Rank;
import com.hms.staff.entity.Role;
import com.hms.staff.entity.Staff;
import com.hms.staff.repository.DepartmentRepository;
import com.hms.staff.repository.RankRepository;
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
            RankRepository rankRepository, DepartmentRepository departmentRepository,
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

            String[] roleNames = { "Super Admin", "Hospital Admin", "Doctor", "Nurse", "Lab Technologist", "Pharmacist",
                    "Radiographer", "Accountant", "Cashier" };
            for (String roleName : roleNames) {
                roleRepository.findByName(roleName).orElseGet(() -> {
                    Role role = new Role();
                    role.setName(roleName);
                    role.setDescription("Default " + roleName + " role");
                    role.setHospitalId(roleName.equals("Super Admin") ? null : 1L);
                    return roleRepository.save(role);
                });
            }

            // 2. Seed Ranks (Pre-saved Designations with Salaries)
            Object[][] ranksData = {
                    { "Medical Officer", new java.math.BigDecimal("250000.00") },
                    { "Senior Medical Officer", new java.math.BigDecimal("350000.00") },
                    { "Nursing Officer", new java.math.BigDecimal("180000.00") },
                    { "Senior Nursing Officer", new java.math.BigDecimal("220000.00") },
                    { "Medical Lab Scientist", new java.math.BigDecimal("170000.00") },
                    { "Pharmacist", new java.math.BigDecimal("200000.00") }
            };

            for (Object[] rankData : ranksData) {
                rankRepository.findByName((String) rankData[0]).orElseGet(() -> {
                    Rank rank = new Rank();
                    rank.setName((String) rankData[0]);
                    rank.setHospitalId(1L);
                    rank.setBasicSalary((java.math.BigDecimal) rankData[1]);
                    return rankRepository.save(rank);
                });
            }

            String[] departmentNames = {
                    "Administration", "OPD", "Emergency", "Pediatrics",
                    "Surgery", "Medical", "Laboratory", "Pharmacy",
                    "Radiology", "Maternity", "ICU", "Cardiology"
            };

            for (String deptName : departmentNames) {
                departmentRepository.findByName(deptName).orElseGet(() -> {
                    Department dept = new Department();
                    dept.setName(deptName);
                    dept.setHospitalId(1L);
                    dept.setDescription(deptName + " Department");
                    return departmentRepository.save(dept);
                });
            }

            // 4. Create the SaaS Super Admin User
            if (!staffRepository.existsByEmail("superadmin@hms-saas.com")) {
                Staff superAdmin = Staff.builder()
                        .hospitalId(null) // Belongs to the SaaS platform, not a specific hospital
                        .staffId("SA-0001")
                        .fullName("SaaS Super Administrator")
                        .email("superadmin@hms-saas.com")
                        .password(passwordEncoder.encode("super123"))
                        .role(superAdminRole)
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