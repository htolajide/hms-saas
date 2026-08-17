package com.hms.staff.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StaffResponseDto {
    private Long id;
    private String staffId;
    private String fullName;
    private String email;
    private String roleName;
    private String department;
    private String designation;
    private BigDecimal basicSalary;
    private String phone;
}