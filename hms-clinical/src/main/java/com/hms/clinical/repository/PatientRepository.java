package com.hms.clinical.repository;

import com.hms.clinical.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    Optional<Patient> findByPatientId(String patientId);

    boolean existsByPatientId(String patientId);

    List<Patient> findByHospitalId(Long hospitalId);

    List<Patient> findByFullNameContainingIgnoreCase(String fullName);
}