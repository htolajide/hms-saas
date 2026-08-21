package com.hms.clinical.dto;

import lombok.Data;

@Data
public class PrescriptionRequestDto {
    private Long hospitalId;
    private String medicationName;
    private String dosage;
    private String frequency;
    private String duration;
    private String instructions;
    private Integer quantity;
}