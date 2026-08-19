package com.hms.auth.service;

import com.hms.staff.entity.Staff;
import com.hms.staff.repository.StaffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final StaffRepository staffRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void generateResetToken(String email) {
        Staff staff = staffRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("No account found with that email address."));

        String token = UUID.randomUUID().toString();
        staff.setResetToken(token);
        staff.setResetTokenExpiry(LocalDateTime.now().plusHours(1)); // Token valid for 1 hour

        staffRepository.save(staff);

        // --- SIMULATE EMAIL SENDING ---
        // In production, replace this with your EmailService (e.g., SendGrid, AWS SES)
        System.out.println("======================================================");
        System.out.println("🔑 PASSWORD RESET REQUESTED FOR: " + email);
        System.out.println("🔗 RESET URL: http://localhost:5173/reset-password?token=" + token);
        System.out.println("======================================================");
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        Staff staff = staffRepository.findByResetToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid reset token."));

        if (staff.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Reset token has expired. Please request a new one.");
        }

        staff.setPassword(passwordEncoder.encode(newPassword));
        staff.setResetToken(null);
        staff.setResetTokenExpiry(null);

        staffRepository.save(staff);
    }
}