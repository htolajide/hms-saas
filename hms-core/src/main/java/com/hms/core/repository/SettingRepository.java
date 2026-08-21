package com.hms.core.repository;

import com.hms.core.entity.Setting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SettingRepository extends JpaRepository<Setting, Long> {
    List<Setting> findByHospitalIdAndCategoryAndIsActiveTrue(Long hospitalId, String category);

    // For validation during creation
    boolean existsByHospitalIdAndCategoryAndKey(Long hospitalId, String category, String key);
}