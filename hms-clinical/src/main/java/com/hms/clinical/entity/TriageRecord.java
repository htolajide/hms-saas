package com.hms.clinical.entity;

import com.hms.core.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "triage_records")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TriageRecord extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @Column(name = "hospital_id", nullable = false)
    private Long hospitalId;

    // Vitals
    @Column(name = "temperature", precision = 4, scale = 2)
    private BigDecimal temperature; // in Celsius

    @Column(name = "blood_pressure_systolic")
    private Integer bloodPressureSystolic; // e.g., 120

    @Column(name = "blood_pressure_diastolic")
    private Integer bloodPressureDiastolic; // e.g., 80

    @Column(name = "pulse_rate")
    private Integer pulseRate; // bpm

    @Column(name = "respiratory_rate")
    private Integer respiratoryRate; // breaths/min

    @Column(name = "weight", precision = 5, scale = 2)
    private BigDecimal weight; // kg

    @Column(name = "height", precision = 5, scale = 2)
    private BigDecimal height; // cm

    // Calculated
    @Column(name = "bmi", precision = 5, scale = 2)
    private BigDecimal bmi;

    // Clinical Info
    @Column(name = "chief_complaint", columnDefinition = "text")
    private String chiefComplaint;

    @Column(name = "triage_category")
    private String triageCategory; // EMERGENCY, URGENT, NON_URGENT

    @Column(name = "notes", columnDefinition = "text")
    private String notes;
}