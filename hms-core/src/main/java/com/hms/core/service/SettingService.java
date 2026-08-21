package com.hms.core.service;

import com.hms.core.entity.Setting;
import com.hms.core.repository.SettingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SettingService {

    private final SettingRepository settingRepo;

    public List<Setting> getSettingsByCategory(Long hospitalId, String category) {
        return settingRepo.findByHospitalIdAndCategoryAndIsActiveTrue(hospitalId, category);
    }

    @Transactional
    public Setting createOrUpdateSetting(Setting setting) {
        if (settingRepo.existsByHospitalIdAndCategoryAndKey(
                setting.getHospitalId(), setting.getCategory(), setting.getKey())) {
            throw new RuntimeException("Setting already exists: " + setting.getCategory() + "/" + setting.getKey());
        }
        return settingRepo.save(setting);
    }

    @Transactional
    public void deactivateSetting(Long id) {
        Setting setting = settingRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Setting not found"));
        setting.setIsActive(false);
        settingRepo.save(setting);
    }
}