package com.hms.clinical.repository;

import com.hms.clinical.entity.Consultation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ConsultationRepository extends JpaRepository<Consultation, Long> {

    // Find all consultations for a specific patient (used in the UI)
    List<Consultation> findByPatientId(Long patientId);

    // Find all consultations for a specific hospital
    List<Consultation> findByHospitalId(Long hospitalId);

    // Find all consultations done by a specific doctor
    List<Consultation> findByDoctorId(Long doctorId);
}