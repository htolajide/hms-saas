package com.hms.clinical.entity;

import com.hms.core.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "medication_masters")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicationMaster extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "hospital_id", nullable = false)
    private Long hospitalId;

    @Column(nullable = false)
    private String genericName; // e.g., "Paracetamol"

    @Column(nullable = false)
    private String brandName; // e.g., "Panadol Extra"

    @Column(nullable = false)
    private String strength; // e.g., "500mg"

    @Column(nullable = false)
    private String dosageForm; // e.g., "Tablet", "Syrup", "Injection"

    @Column(name = "category_key", nullable = false)
    private String categoryKey;

    @Column(precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Column(unique = true) // Prevents exact duplicates within same hospital
    private String code; // Internal SKU/Code
}