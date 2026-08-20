package com.hms.clinical.repository;

import com.hms.clinical.entity.TriageRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TriageRepository extends JpaRepository<TriageRecord, Long> {
    List<TriageRecord> findByPatientId(Long patientId);

    List<TriageRecord> findByHospitalId(Long hospitalId);
}