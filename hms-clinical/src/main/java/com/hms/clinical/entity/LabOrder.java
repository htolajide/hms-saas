package com.hms.clinical.entity;

import com.hms.core.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "lab_orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LabOrder extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "hospital_id", nullable = false)
    private Long hospitalId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consultation_id", nullable = false)
    private Consultation consultation;

    @Column(name = "test_name", nullable = false)
    private String testName; // e.g., "Complete Blood Count"

    @Column(name = "test_code")
    private String testCode;

    @Column(columnDefinition = "text")
    private String notes;

    @Column(name = "status")
    private String status = "PENDING"; // PENDING, COMPLETED, CANCELLED

    @Column(name = "result", columnDefinition = "text")
    private String result;
}