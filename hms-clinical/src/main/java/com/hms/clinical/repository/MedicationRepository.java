package com.hms.clinical.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hms.clinical.entity.Medication;

@Repository
public interface MedicationRepository extends JpaRepository<Medication, Long> {
    List<Medication> findByHospitalId(Long hospitalId);

    Optional<Medication> findByHospitalIdAndMasterId(Long hospitalId, Long masterId);
}
