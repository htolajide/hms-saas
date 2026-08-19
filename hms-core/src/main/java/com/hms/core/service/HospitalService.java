package com.hms.core.service;

import com.hms.core.dto.HospitalRequestDto;
import com.hms.core.entity.Hospital;
import com.hms.core.repository.HospitalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HospitalService {

    private final HospitalRepository hospitalRepository;

    @Transactional
    public Hospital createHospital(HospitalRequestDto dto) {
        if (hospitalRepository.existsByHospitalCode(dto.getHospitalCode())) {
            throw new RuntimeException("Hospital code already exists: " + dto.getHospitalCode());
        }

        Hospital hospital = Hospital.builder()
                .hospitalCode(dto.getHospitalCode())
                .name(dto.getName())
                .address(dto.getAddress())
                .phone(dto.getPhone())
                .email(dto.getEmail())
                .isActive(true)
                .build();

        return hospitalRepository.save(hospital);
    }
}