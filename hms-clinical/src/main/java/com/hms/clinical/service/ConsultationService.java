package com.hms.clinical.service;

import com.hms.clinical.dto.*;
import com.hms.clinical.entity.*;
import com.hms.clinical.repository.*;
import com.hms.staff.entity.Staff;
import com.hms.staff.repository.StaffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConsultationService {

    private final ConsultationRepository consultationRepository;
    private final PatientRepository patientRepository;
    private final StaffRepository staffRepository;

    @Transactional
    public ConsultationResponseDto createConsultation(ConsultationRequestDto dto) {
        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        Staff doctor = staffRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        Consultation consultation = Consultation.builder()
                .patient(patient)
                .doctor(doctor)
                .hospitalId(dto.getHospitalId())
                .consultationDate(dto.getConsultationDate())
                .subjective(dto.getSubjective())
                .objective(dto.getObjective())
                .assessment(dto.getAssessment())
                .plan(dto.getPlan())
                .notes(dto.getNotes())
                .build();

        // Add Prescriptions
        if (dto.getPrescriptions() != null) {
            for (PrescriptionRequestDto presDto : dto.getPrescriptions()) {
                Prescription prescription = Prescription.builder()
                        .consultation(consultation)
                        .medicationName(presDto.getMedicationName())
                        .dosage(presDto.getDosage())
                        .frequency(presDto.getFrequency())
                        .duration(presDto.getDuration())
                        .instructions(presDto.getInstructions())
                        .quantity(presDto.getQuantity())
                        .build();
                consultation.getPrescriptions().add(prescription);
            }
        }

        // Add Lab Orders
        if (dto.getLabOrders() != null) {
            for (LabOrderRequestDto labDto : dto.getLabOrders()) {
                LabOrder labOrder = LabOrder.builder()
                        .consultation(consultation)
                        .testName(labDto.getTestName())
                        .testCode(labDto.getTestCode())
                        .notes(labDto.getNotes())
                        .status("PENDING")
                        .build();
                consultation.getLabOrders().add(labOrder);
            }
        }

        return mapToDto(consultationRepository.save(consultation));
    }

    public ConsultationResponseDto getConsultationById(Long id) {
        Consultation consultation = consultationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Consultation not found"));
        return mapToDto(consultation);
    }

    public List<ConsultationResponseDto> getConsultationsByPatientId(Long patientId) {
        return consultationRepository.findByPatientId(patientId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private ConsultationResponseDto mapToDto(Consultation c) {
        return ConsultationResponseDto.builder()
                .id(c.getId())
                .patientId(c.getPatient().getPatientId())
                .patientName(c.getPatient().getFullName())
                .doctorId(c.getDoctor().getId().toString())
                .doctorName(c.getDoctor().getFullName())
                .hospitalId(c.getHospitalId())
                .consultationDate(c.getConsultationDate())
                .subjective(c.getSubjective())
                .objective(c.getObjective())
                .assessment(c.getAssessment())
                .plan(c.getPlan())
                .notes(c.getNotes())
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .prescriptions(c.getPrescriptions().stream()
                        .map(p -> PrescriptionResponseDto.builder()
                                .id(p.getId())
                                .medicationName(p.getMedicationName())
                                .dosage(p.getDosage())
                                .frequency(p.getFrequency())
                                .duration(p.getDuration())
                                .instructions(p.getInstructions())
                                .quantity(p.getQuantity())
                                .build())
                        .collect(Collectors.toList()))
                .labOrders(c.getLabOrders().stream()
                        .map(l -> LabOrderResponseDto.builder()
                                .id(l.getId())
                                .testName(l.getTestName())
                                .testCode(l.getTestCode())
                                .notes(l.getNotes())
                                .status(l.getStatus())
                                .result(l.getResult())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }
}