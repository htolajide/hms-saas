package com.hms.clinical.dto;

import lombok.Data;

@Data
public class MedicationStockRequestDto {
    private Long hospitalId;
    private Long masterId; // Links to MedicationMaster
    private Integer stockLevel;
    private Integer reorderLevel;
}