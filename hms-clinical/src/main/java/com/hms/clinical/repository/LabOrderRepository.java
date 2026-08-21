package com.hms.clinical.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hms.clinical.entity.LabOrder;

@Repository
public interface LabOrderRepository extends JpaRepository<LabOrder, Long> {
    List<LabOrder> findByHospitalIdAndStatus(Long hospitalId, String status);
}
