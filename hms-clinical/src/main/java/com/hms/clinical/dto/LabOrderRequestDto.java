package com.hms.clinical.dto;

import lombok.Data;

@Data
public class LabOrderRequestDto {
    private String testName;
    private String testCode;
    private String notes;
}