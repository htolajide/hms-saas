package com.hms.clinical.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hms.clinical.entity.MedicationMaster;

@Repository
public interface MedicationMasterRepository extends JpaRepository<MedicationMaster, Long> {
    List<MedicationMaster> findByHospitalId(Long hospitalId);

    Optional<MedicationMaster> findByHospitalIdAndGenericNameAndStrengthAndDosageForm(
            Long hospitalId, String genericName, String strength, String dosageForm);
}