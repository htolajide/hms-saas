package com.hms.clinical.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class LabTestResponseDto {
    private Long id;
    private String name;
    private String categoryKey;
    private String categoryLabel; // ← Resolved from Settings at runtime
    private BigDecimal price;
}