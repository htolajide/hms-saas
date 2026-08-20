package com.hms.clinical.service;

import com.hms.clinical.dto.TriageRequestDto;
import com.hms.clinical.dto.TriageResponseDto;
import com.hms.clinical.entity.Patient;
import com.hms.clinical.entity.TriageRecord;
import com.hms.clinical.repository.PatientRepository;
import com.hms.clinical.repository.TriageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TriageService {

    private final TriageRepository triageRepository;
    private final PatientRepository patientRepository;

    @Transactional
    public TriageResponseDto createTriage(TriageRequestDto dto) {
        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        // Calculate BMI: weight (kg) / (height (m))^2
        BigDecimal bmi = null;
        if (dto.getWeight() != null && dto.getHeight() != null) {
            BigDecimal heightInMeters = dto.getHeight().divide(BigDecimal.valueOf(100));
            bmi = dto.getWeight()
                    .divide(heightInMeters.pow(2), 2, RoundingMode.HALF_UP);
        }

        TriageRecord triage = TriageRecord.builder()
                .patient(patient)
                .hospitalId(dto.getHospitalId())
                .temperature(dto.getTemperature())
                .bloodPressureSystolic(dto.getBloodPressureSystolic())
                .bloodPressureDiastolic(dto.getBloodPressureDiastolic())
                .pulseRate(dto.getPulseRate())
                .respiratoryRate(dto.getRespiratoryRate())
                .weight(dto.getWeight())
                .height(dto.getHeight())
                .bmi(bmi)
                .chiefComplaint(dto.getChiefComplaint())
                .triageCategory(dto.getTriageCategory())
                .notes(dto.getNotes())
                .build();

        return mapToDto(triageRepository.save(triage));
    }

    public List<TriageResponseDto> getTriageByPatient(Long patientId) {
        return triageRepository.findByPatientId(patientId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private TriageResponseDto mapToDto(TriageRecord record) {
        return TriageResponseDto.builder()
                .id(record.getId())
                .patientId(record.getPatient().getPatientId())
                .patientName(record.getPatient().getFullName())
                .temperature(record.getTemperature())
                .bloodPressureSystolic(record.getBloodPressureSystolic())
                .bloodPressureDiastolic(record.getBloodPressureDiastolic())
                .pulseRate(record.getPulseRate())
                .respiratoryRate(record.getRespiratoryRate())
                .weight(record.getWeight())
                .height(record.getHeight())
                .bmi(record.getBmi())
                .chiefComplaint(record.getChiefComplaint())
                .triageCategory(record.getTriageCategory())
                .notes(record.getNotes())
                .createdAt(record.getCreatedAt())
                .build();
    }
}