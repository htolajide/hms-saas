package com.hms.clinical.entity;

import com.hms.core.entity.BaseEntity;
import com.hms.staff.entity.Staff;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "consultations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Consultation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Staff doctor;

    @Column(name = "hospital_id", nullable = false)
    private Long hospitalId;

    @Column(name = "consultation_date", nullable = false)
    private LocalDateTime consultationDate;

    // Clinical Notes
    @Column(name = "subjective", columnDefinition = "text")
    private String subjective; // Patient's description of symptoms

    @Column(name = "objective", columnDefinition = "text")
    private String objective; // Doctor's observations

    @Column(name = "assessment", columnDefinition = "text")
    private String assessment; // Diagnosis

    @Column(name = "plan", columnDefinition = "text")
    private String plan; // Treatment plan

    @Column(name = "notes", columnDefinition = "text")
    private String notes;

    @Column(name = "status", nullable = false)
    private String status = "PENDING"; // PENDING, IN_PROGRESS, CLOSED

    // Relationships
    @OneToMany(mappedBy = "consultation", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Prescription> prescriptions = new ArrayList<>();

    @OneToMany(mappedBy = "consultation", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<LabOrder> labOrders = new ArrayList<>();
}