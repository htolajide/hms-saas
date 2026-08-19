package com.hms.api.dto;

import lombok.Data;

@Data
public class HospitalRegistrationRequestDto {
    // Hospital Details
    private String hospitalName;
    private String hospitalCode; // e.g., HOSP-002
    private String address;
    private String phone;

    // Initial Admin Details
    private String adminFullName;
    private String adminEmail;
    private String adminPassword;
}