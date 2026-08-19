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
    private Long roleId;
    private Long departmentId;
    private Long rankId;
    private String staffId;
    private String fullName;
    private String email;
    private String roleName;
    private String departmentName;
    private String rankName;
    private BigDecimal basicSalary; // We still send this to the frontend to display
    private String phone;
    private String passportPhoto;
    private String qualification;
}