package com.hms.clinical.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ConsultationResponseDto {
    private Long id;
    private String patientId;
    private String patientName;
    private String doctorId;
    private String doctorName;
    private Long hospitalId;
    private LocalDateTime consultationDate;

    // SOAP Notes
    private String subjective;
    private String objective;
    private String assessment;
    private String plan;
    private String notes;

    // Relationships
    private List<PrescriptionResponseDto> prescriptions;
    private List<LabOrderResponseDto> labOrders;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}