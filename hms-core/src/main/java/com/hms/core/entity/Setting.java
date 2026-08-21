package com.hms.core.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "hospital_settings", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "hospital_id", "category", "key" })
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Setting extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "hospital_id", nullable = false)
    private Long hospitalId;

    // Groups settings logically: "LAB_TEST_CATEGORY", "MEDICATION_CATEGORY",
    // "DEPARTMENT", "TRIAGE_CATEGORY", "BLOOD_GROUP", etc.
    @Column(nullable = false)
    private String category;

    // The actual value stored: "Hematology", "Antibiotic", "OPD", "EMERGENCY"
    @Column(nullable = false)
    private String key;

    // Human-readable label for UI display
    @Column(nullable = false)
    private String label;

    // Optional sort order for consistent dropdown ordering
    private Integer sortOrder = 0;

    // Allows admin to deactivate without deleting (preserves historical data)
    private Boolean isActive = true;
}