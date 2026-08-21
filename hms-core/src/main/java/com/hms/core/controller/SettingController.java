package com.hms.core.controller;

import com.hms.core.entity.Setting;
import com.hms.core.service.SettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/settings")
@RequiredArgsConstructor
public class SettingController {

    private final SettingService settingService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_SUPER_ADMIN', 'ROLE_HOSPITAL_ADMIN')")
    public ResponseEntity<List<Setting>> getSettings(
            @RequestParam Long hospitalId,
            @RequestParam String category) {
        return ResponseEntity.ok(settingService.getSettingsByCategory(hospitalId, category));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_HOSPITAL_ADMIN')")
    public ResponseEntity<Setting> createSetting(@RequestBody Setting setting) {
        return ResponseEntity.ok(settingService.createOrUpdateSetting(setting));
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasAuthority('ROLE_HOSPITAL_ADMIN')")
    public ResponseEntity<?> deactivateSetting(@PathVariable Long id) {
        settingService.deactivateSetting(id);
        return ResponseEntity.ok("Setting deactivated");
    }
}