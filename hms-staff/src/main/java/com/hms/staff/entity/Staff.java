package com.hms.staff.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hms.core.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;

@Entity
@Table(name = "staff")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Staff extends BaseEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long hospitalId;

    @Column(name = "staff_id", unique = true)
    private String staffId;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    @JsonIgnore // ✅ ADD THIS
    private String password;

    // --- ROLE MAPPING ---
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id")
    private Role role;

    // --- RANK MAPPING (The proper way) ---
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rank_id")
    private Rank rank;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "department_id")
    private Department department;

    private String phone;
    private String qualification;

    @Column(name = "joined_date")
    private LocalDate joinedDate;

    @Column(name = "passport_photo", columnDefinition = "text")
    private String passportPhoto;

    private String resetToken;

    private java.time.LocalDateTime resetTokenExpiry;

    // --- UserDetails Methods ---
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String roleName = (role != null) ? role.getName().toUpperCase().replace(" ", "_") : "USER";
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + roleName));
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}