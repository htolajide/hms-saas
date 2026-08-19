package com.hms.staff.entity;

import com.hms.core.entity.BaseEntity;
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
import java.math.BigDecimal;

@Entity
@Table(name = "ranks")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rank extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long hospitalId;

    @Column(nullable = false, unique = true)
    private String name; // e.g., "Medical Officer", "Senior Nursing Officer"

    @Column(precision = 12, scale = 2, nullable = false)
    private BigDecimal basicSalary;
}