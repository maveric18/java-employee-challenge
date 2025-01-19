package com.reliaquest.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class EmployeeListResponse {

    private String status;
    private List<EmployeeModel> data;
}