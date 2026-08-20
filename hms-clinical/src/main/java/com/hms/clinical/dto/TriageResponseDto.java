package com.hms.clinical.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class TriageResponseDto {
    private Long id;
    private String patientId;
    private String patientName;
    private BigDecimal temperature;
    private Integer bloodPressureSystolic;
    private Integer bloodPressureDiastolic;
    private Integer pulseRate;
    private Integer respiratoryRate;
    private BigDecimal weight;
    private BigDecimal height;
    private BigDecimal bmi;
    private String chiefComplaint;
    private String triageCategory;
    private String notes;
    private LocalDateTime createdAt;
}