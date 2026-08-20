package com.hms.clinical.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ConsultationRequestDto {
    private Long patientId;
    private Long doctorId;
    private Long hospitalId;
    private LocalDateTime consultationDate;

    private String subjective;
    private String objective;
    private String assessment;
    private String plan;
    private String notes;

    private List<PrescriptionRequestDto> prescriptions;
    private List<LabOrderRequestDto> labOrders;
}