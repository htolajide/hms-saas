package com.hms.clinical.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LabOrderResponseDto {
    private Long id;
    private String testName;
    private String testCode;
    private String notes;
    private String status; // PENDING, COMPLETED, CANCELLED
    private String result;
}