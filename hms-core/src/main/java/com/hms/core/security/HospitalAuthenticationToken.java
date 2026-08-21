package com.hms.core.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class HospitalAuthenticationToken extends AbstractAuthenticationToken {

    private final Object principal; // Usually the UserDetails or username
    private final Long hospitalId;

    public HospitalAuthenticationToken(Object principal, Long hospitalId,
            Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.hospitalId = hospitalId;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null; // Password is cleared after auth
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }

    public Long getHospitalId() {
        return this.hospitalId;
    }

    public boolean isSuperAdmin() {
        return this.hospitalId == null;
    }
}