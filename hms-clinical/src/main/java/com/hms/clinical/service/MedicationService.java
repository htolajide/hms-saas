package com.hms.clinical.service;

import com.hms.clinical.dto.MedicationMasterResponseDto;
import com.hms.clinical.dto.MedicationStockRequestDto;
import com.hms.clinical.entity.Medication;
import com.hms.clinical.entity.MedicationMaster;
import com.hms.clinical.repository.MedicationMasterRepository;
import com.hms.clinical.repository.MedicationRepository;
import com.hms.core.entity.Setting;
import com.hms.core.repository.SettingRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MedicationService {
    private final MedicationRepository medRepo;
    private final MedicationMasterRepository masterRepo;
    private final SettingRepository settingRepo;

    public List<Medication> getMedicationsByHospital(Long hospitalId) {
        // Eager fetch master + category settings for display
        return medRepo.findByHospitalId(hospitalId);
    }

    /**
     * Adds inventory stock for an existing MedicationMaster.
     * Prevents duplicate inventory records for the same master in a hospital.
     */
    @Transactional
    public Medication addStockToMaster(MedicationStockRequestDto dto) {
        MedicationMaster master = masterRepo.findById(dto.getMasterId())
                .orElseThrow(() -> new RuntimeException("Medication Master not found: " + dto.getMasterId()));

        // Check if inventory record already exists for this master + hospital
        return medRepo.findByHospitalIdAndMasterId(dto.getHospitalId(), dto.getMasterId())
                .map(existing -> {
                    // Update existing stock levels
                    existing.setStockLevel(dto.getStockLevel());
                    existing.setReorderLevel(dto.getReorderLevel());
                    return medRepo.save(existing);
                })
                .orElseGet(() -> {
                    // Create new inventory record linked to master
                    Medication medication = Medication.builder()
                            .hospitalId(dto.getHospitalId())
                            .master(master)
                            .stockLevel(dto.getStockLevel())
                            .reorderLevel(dto.getReorderLevel())
                            .build();
                    return medRepo.save(medication);
                });
    }

    @Transactional
    public void deleteMedication(Long id) {
        medRepo.deleteById(id);
    }

    public List<MedicationMasterResponseDto> getMasterCatalog(Long hospitalId) {
        List<MedicationMaster> masters = masterRepo.findByHospitalId(hospitalId);

        List<Setting> categories = settingRepo.findByHospitalIdAndCategoryAndIsActiveTrue(
                hospitalId, "MEDICATION_CATEGORY");
        Map<String, String> labelMap = categories.stream()
                .collect(Collectors.toMap(Setting::getKey, Setting::getLabel));

        return masters.stream().map(m -> MedicationMasterResponseDto.builder()
                .id(m.getId())
                .genericName(m.getGenericName())
                .brandName(m.getBrandName())
                .strength(m.getStrength())
                .dosageForm(m.getDosageForm())
                .categoryKey(m.getCategoryKey())
                .categoryLabel(labelMap.getOrDefault(m.getCategoryKey(), m.getCategoryKey())) // Fallback to key if
                                                                                              // label missing
                .unitPrice(m.getUnitPrice())
                .code(m.getCode())
                .build()).collect(Collectors.toList());
    }

    @Transactional
    public MedicationMaster createMaster(MedicationMaster master) {
        // Auto-generate code if not provided
        if (master.getCode() == null || master.getCode().isEmpty()) {
            long count = masterRepo.count() + 1;
            master.setCode("MED-" + String.format("%04d", count));
        }
        return masterRepo.save(master);
    }
}