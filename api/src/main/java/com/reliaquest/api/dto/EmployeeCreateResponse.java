package com.reliaquest.api.dto;

import com.reliaquest.api.model.Employee;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployeeCreateResponse {
    private String status;
    private Employee data;
    private String message;
}