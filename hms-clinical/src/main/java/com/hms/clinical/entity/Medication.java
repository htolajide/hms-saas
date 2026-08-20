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

    @Column(nullable = false)
    private String name;
    private String category;
    private String manufacturer;

    @Column(precision = 10, scale = 2)
    private BigDecimal price;

    // Inventory Fields
    private Integer stockLevel = 0;
    private Integer reorderLevel = 10;
}