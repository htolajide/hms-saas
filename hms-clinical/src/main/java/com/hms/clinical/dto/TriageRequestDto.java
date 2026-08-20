package com.hms.clinical.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class TriageRequestDto {
    private Long patientId;
    private Long hospitalId;

    // Vitals
    private BigDecimal temperature;
    private Integer bloodPressureSystolic;
    private Integer bloodPressureDiastolic;
    private Integer pulseRate;
    private Integer respiratoryRate;
    private BigDecimal weight;
    private BigDecimal height;

    // Clinical
    private String chiefComplaint;
    private String triageCategory;
    private String notes;
}