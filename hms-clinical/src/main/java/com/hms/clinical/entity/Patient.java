package com.hms.clinical.entity;

import com.hms.core.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "patients")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Patient extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "hospital_id", nullable = false)
    private Long hospitalId;

    @Column(name = "patient_id", unique = true, nullable = false)
    private String patientId; // e.g., PAT-0001

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Column(nullable = false)
    private String gender; // MALE, FEMALE

    @Column(name = "blood_group")
    private String bloodGroup; // e.g., O+, A-, B+

    private String phone;
    private String email;
    private String address;

    // Next of Kin Information
    @Column(name = "nok_name")
    private String nextOfKinName;

    @Column(name = "nok_phone")
    private String nextOfKinPhone;

    @Column(name = "nok_relationship")
    private String nextOfKinRelationship;
}