package com.reliaquest.api.dto;

import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
public class MockEmployee {
    private UUID id;
    private String employee_name;
    private Integer employee_salary;
    private Integer employee_age;
    private String employee_title;
    private String employee_email;
}
