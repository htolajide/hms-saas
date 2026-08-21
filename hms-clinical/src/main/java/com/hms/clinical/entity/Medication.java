package com.hms.clinical.entity;

import com.hms.core.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "medications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Medication extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "hospital_id", nullable = false)
    private Long hospitalId;

    // In Medication.java - REPLACE name, category, price fields with:
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "master_id", nullable = false)
    private MedicationMaster master;

    private Integer stockLevel = 0;
    private Integer reorderLevel = 10;

    private String manufacturer;

}