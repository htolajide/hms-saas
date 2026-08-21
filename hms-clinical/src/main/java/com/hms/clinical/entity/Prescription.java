package com.hms.clinical.entity;

import com.hms.core.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "prescriptions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Prescription extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "hospital_id", nullable = false)
    private Long hospitalId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consultation_id", nullable = false)
    private Consultation consultation;

    @Column(name = "medication_name", nullable = false)
    private String medicationName;

    @Column(name = "dosage", nullable = false)
    private String dosage; // e.g., "500mg"

    @Column(name = "frequency", nullable = false)
    private String frequency; // e.g., "Twice daily"

    @Column(name = "duration", nullable = false)
    private String duration; // e.g., "7 days"

    @Column(name = "instructions", columnDefinition = "text")
    private String instructions; // e.g., "Take after meals"

    @Column(name = "quantity")
    private Integer quantity;
}