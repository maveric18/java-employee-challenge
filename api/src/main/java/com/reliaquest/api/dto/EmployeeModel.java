package com.reliaquest.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.reliaquest.api.model.Employee;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.UUID;


@Getter
@Setter
@NoArgsConstructor
public class EmployeeModel {
    @JsonProperty("id")
    private UUID id;
    @JsonProperty("employee_name")
    private String name;
    @JsonProperty("employee_salary")
    private Integer salary;
    @JsonProperty("employee_age")
    private Integer age;
    @JsonProperty("employee_title")
    private String title;
    @JsonProperty("employee_email")
    private String email;

    public EmployeeModel(UUID id, String name, int salary, int age, String title, String email) {
        this.id = id;
        this.name = name;
        this.salary = salary;
        this.age = age;
        this.title = title;
        this.email = email;
    }

    public static Employee convertEmployeeModelToEmployee(EmployeeModel employeeModel) {

        Employee employee = new Employee();
        employee.setId(employeeModel.getId());
        employee.setName(employeeModel.getName());
        employee.setSalary(employeeModel.getSalary());
        employee.setAge(employeeModel.getAge());
        employee.setTitle(employeeModel.getTitle());
        employee.setEmail(employeeModel.getEmail());
        return employee;
    }
}
