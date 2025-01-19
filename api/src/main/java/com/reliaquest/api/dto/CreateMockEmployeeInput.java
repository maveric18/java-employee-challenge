package com.reliaquest.api.dto;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreateMockEmployeeInput {
    @NotNull
    @Size(min = 1, max = 100)
    private String employee_name;
    @NotNull
    private Integer employee_salary;
    @NotNull
    private Integer employee_age;
    @NotNull
    private String employee_title;
    @NotNull
    private String employee_email;
}