package com.hms.clinical.entity;

import com.hms.core.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "medication_categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicationCategory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "hospital_id", nullable = false)
    private Long hospitalId;

    @Column(nullable = false, unique = true)
    private String name; // e.g., "Analgesic", "Antibiotic"

    private String description;
}