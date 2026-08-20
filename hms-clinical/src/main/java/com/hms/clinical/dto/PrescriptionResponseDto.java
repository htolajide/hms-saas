package com.hms.clinical.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PrescriptionResponseDto {
    private Long id;
    private String medicationName;
    private String dosage;
    private String frequency;
    private String duration;
    private String instructions;
    private Integer quantity;
}