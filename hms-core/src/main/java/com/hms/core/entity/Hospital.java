package com.hms.core.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "hospitals")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Hospital extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "hospital_code", unique = true, nullable = false)
    private String hospitalCode; // e.g., "HOSP-001"

    @Column(name = "name", nullable = false)
    private String name; // e.g., "First Mercy Hospital"

    private String address;
    private String phone;
    private String email;

    @Column(name = "is_active")
    private Boolean isActive = true;
}