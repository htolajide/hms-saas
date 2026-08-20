package com.hms.clinical.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class PatientRequestDto {
    private Long hospitalId;
    private String fullName;
    private LocalDate dateOfBirth;
    private String gender; // MALE, FEMALE
    private String bloodGroup;
    private String phone;
    private String email;
    private String address;

    // Next of Kin
    private String nextOfKinName;
    private String nextOfKinPhone;
    private String nextOfKinRelationship;
}