package com.hms.clinical.service;

import com.hms.clinical.dto.PatientRequestDto;
import com.hms.clinical.dto.PatientResponseDto;
import com.hms.clinical.entity.Patient;
import com.hms.clinical.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;

    @Transactional(readOnly = true)
    public List<PatientResponseDto> getAllPatients(Long hospitalId) {
        return patientRepository.findByHospitalId(hospitalId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public PatientResponseDto createPatient(PatientRequestDto dto) {
        // Auto-generate Patient ID
        String patientId = "PAT-" + String.format("%04d", (patientRepository.count() + 1));

        Patient patient = Patient.builder()
                .hospitalId(dto.getHospitalId())
                .patientId(patientId)
                .fullName(dto.getFullName())
                .dateOfBirth(dto.getDateOfBirth())
                .gender(dto.getGender())
                .bloodGroup(dto.getBloodGroup())
                .phone(dto.getPhone())
                .email(dto.getEmail())
                .address(dto.getAddress())
                .nextOfKinName(dto.getNextOfKinName())
                .nextOfKinPhone(dto.getNextOfKinPhone())
                .nextOfKinRelationship(dto.getNextOfKinRelationship())
                .build();

        return mapToDto(patientRepository.save(patient));
    }

    private PatientResponseDto mapToDto(Patient patient) {
        return PatientResponseDto.builder()
                .id(patient.getId())
                .patientId(patient.getPatientId())
                .fullName(patient.getFullName())
                .dateOfBirth(patient.getDateOfBirth())
                .gender(patient.getGender())
                .bloodGroup(patient.getBloodGroup())
                .phone(patient.getPhone())
                .email(patient.getEmail())
                .address(patient.getAddress())
                .nextOfKinName(patient.getNextOfKinName())
                .nextOfKinPhone(patient.getNextOfKinPhone())
                .nextOfKinRelationship(patient.getNextOfKinRelationship())
                .build();
    }
}