package com.hms.clinical.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class MedicationMasterResponseDto {
    private Long id;
    private String genericName;
    private String brandName;
    private String strength;
    private String dosageForm;
    private String categoryKey;
    private String categoryLabel; // ← Resolved from Settings
    private BigDecimal unitPrice;
    private String code;
}