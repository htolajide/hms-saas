package com.hms.clinical.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;

@Data
@Builder
public class PatientResponseDto {
    private Long id;
    private String patientId;
    private String fullName;
    private LocalDate dateOfBirth;
    private String gender;
    private String bloodGroup;
    private String phone;
    private String email;
    private String address;
    private String nextOfKinName;
    private String nextOfKinPhone;
    private String nextOfKinRelationship;
}