package com.hms.auth.filter;

import com.hms.auth.service.CustomUserDetailsService;
import com.hms.auth.service.JwtService;
import com.hms.core.security.HospitalAuthenticationToken;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, CustomUserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String uri = request.getRequestURI();
        String authHeader = request.getHeader("Authorization");

        System.out.println("🔍 JWT FILTER DEBUG | URI: " + uri);
        System.out.println("🔍 JWT FILTER DEBUG | Auth Header Present: " + (authHeader != null));
        System.out.println("🔍 JWT FILTER DEBUG | Auth Header Starts With Bearer: " +
                (authHeader != null && authHeader.startsWith("Bearer ")));

        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                System.out.println("️ JWT FILTER: Skipping - No valid Bearer token");
                filterChain.doFilter(request, response);
                return;
            }

            String token = authHeader.substring(7);
            String username = jwtService.extractUsername(token);

            System.out.println("🔍 JWT FILTER DEBUG | Extracted Username: " + username);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                boolean isValid = jwtService.isTokenValid(token, userDetails);
                System.out.println("🔍 JWT FILTER DEBUG | Token Valid: " + isValid);

                if (isValid) {
                    Long hospitalId = jwtService.extractHospitalId(token);
                    HospitalAuthenticationToken authToken = new HospitalAuthenticationToken(
                            userDetails, hospitalId, userDetails.getAuthorities());

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    System.out.println("✅ JWT FILTER: Authentication SET for " + username +
                            " | HospitalID: " + hospitalId);
                }
            }
        } catch (Exception e) {
            System.err.println("❌ JWT FILTER EXCEPTION: " + e.getMessage());
            e.printStackTrace();
        }

        filterChain.doFilter(request, response);
    }
}