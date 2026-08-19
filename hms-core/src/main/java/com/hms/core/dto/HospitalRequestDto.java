package com.hms.core.dto;

import lombok.Data;

@Data
public class HospitalRequestDto {
    private String hospitalCode;
    private String name;
    private String address;
    private String phone;
    private String email;
}