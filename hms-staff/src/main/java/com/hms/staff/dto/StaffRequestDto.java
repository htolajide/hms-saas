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
public class StaffRequestDto {
    private Long hospitalId;
    private String staffId;
    private String fullName;
    private String email;
    private String password; // Only used during creation
    private Long roleId; // We pass the ID of the role
    private String department;
    private String designation;
    private BigDecimal basicSalary;
    private String phone;
}