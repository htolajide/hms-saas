package com.hms.clinical.repository;

import com.hms.clinical.entity.MedicationCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicationCategoryRepository extends JpaRepository<MedicationCategory, Long> {
    List<MedicationCategory> findByHospitalId(Long hospitalId);
}