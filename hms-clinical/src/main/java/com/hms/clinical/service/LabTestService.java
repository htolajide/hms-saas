package com.hms.clinical.service;

import com.hms.clinical.dto.LabTestRequestDto;
import com.hms.clinical.dto.LabTestResponseDto;
import com.hms.clinical.entity.LabOrder;
import com.hms.clinical.entity.LabTest;
import com.hms.clinical.repository.LabOrderRepository;
import com.hms.clinical.repository.LabTestRepository;
import com.hms.core.entity.Setting;
import com.hms.core.repository.SettingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LabTestService {
    private final LabTestRepository testRepo;
    private final LabOrderRepository orderRepo;
    private final SettingRepository settingRepo;

    /**
     * Returns lab tests with resolved category labels for frontend display.
     */
    public List<LabTestResponseDto> getTestsByHospital(Long hospitalId) {
        List<LabTest> tests = testRepo.findByHospitalId(hospitalId);

        // Fetch all active LAB_TEST_CATEGORY settings ONCE (avoids N+1 queries)
        List<Setting> categories = settingRepo.findByHospitalIdAndCategoryAndIsActiveTrue(
                hospitalId, "LAB_TEST_CATEGORY");

        Map<String, String> labelMap = categories.stream()
                .collect(Collectors.toMap(Setting::getKey, Setting::getLabel));

        return tests.stream().map(t -> LabTestResponseDto.builder()
                .id(t.getId())
                .name(t.getName())
                .categoryKey(t.getCategoryKey())
                .categoryLabel(labelMap.getOrDefault(t.getCategoryKey(), t.getCategoryKey())) // Fallback to key if
                                                                                              // label missing
                .price(t.getPrice())
                .build()).collect(Collectors.toList());
    }

    @Transactional
    public LabTest createOrUpdateTest(LabTestRequestDto dto) {
        boolean validCategory = settingRepo.existsByHospitalIdAndCategoryAndKey(
                dto.getHospitalId(), "LAB_TEST_CATEGORY", dto.getCategoryKey());

        if (!validCategory) {
            throw new AccessDeniedException("Invalid lab test category key: " + dto.getCategoryKey());
        }

        LabTest test = LabTest.builder()
                .hospitalId(dto.getHospitalId())
                .name(dto.getName())
                .categoryKey(dto.getCategoryKey())
                .price(dto.getPrice())
                .build();

        return testRepo.save(test);
    }

    public List<LabOrder> getPendingOrders(Long hospitalId) {
        return orderRepo.findByHospitalIdAndStatus(hospitalId, "PENDING");
    }

    @Transactional
    public LabOrder postLabResult(Long orderId, String result) {
        LabOrder order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Lab order not found: " + orderId));

        order.setResult(result);
        order.setStatus("COMPLETED");
        return orderRepo.save(order);
    }
}