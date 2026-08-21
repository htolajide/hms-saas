package com.hms.clinical.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class LabTestRequestDto {
    private Long hospitalId;
    private String name;
    private String categoryKey; // Matches Setting.key where category='LAB_TEST_CATEGORY'
    private BigDecimal price;
}