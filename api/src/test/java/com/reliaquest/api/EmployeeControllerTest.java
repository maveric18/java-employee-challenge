package com.reliaquest.api;

import com.reliaquest.api.controller.EmployeeController;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class EmployeeControllerTest {

    @InjectMocks
    private EmployeeController employeeController;

    @Mock
    private EmployeeService employeeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllEmployees_shouldReturnEmployeeList() throws Exception {
        List<Employee> mockEmployees = Arrays.asList(new Employee(UUID.fromString("4ea6f6dc-eb53-42b8-a6a5-f3fde7ab803a"), "John Doe", 5000, 25, "Engineer", "john@example.com"));
        when(employeeService.getAllEmployees()).thenReturn(mockEmployees);
        ResponseEntity<List<Employee>> response = employeeController.getAllEmployees();
        assertEquals(mockEmployees, response.getBody());
        assertEquals(200, response.getStatusCode().value());
        verify(employeeService, times(1)).getAllEmployees();
    }

    @Test
    void getEmployeesByNameSearch_shouldReturnFilteredEmployeeList() {
        String searchString = "John";
        List<Employee> mockEmployees = Arrays.asList(new Employee(UUID.fromString("4ea6f6dc-eb53-42b8-a6a5-f3fde7ab803a"), "John Doe", 5000, 25, "Engineer", "john@example.com"));
        when(employeeService.filterEmpNameFromSearchString(searchString)).thenReturn(mockEmployees);
        ResponseEntity<List<Employee>> response = employeeController.getEmployeesByNameSearch(searchString);
        assertEquals(mockEmployees, response.getBody());
        assertEquals(200, response.getStatusCode().value());
        verify(employeeService, times(1)).filterEmpNameFromSearchString(searchString);
    }

    @Test
    void getEmployeeById_shouldReturnEmployee() {
        String id = "4ea6f6dc-eb53-42b8-a6a5-f3fde7ab803a";
        Employee mockEmployee = new Employee(UUID.fromString(id), "John Doe", 5000, 25, "Engineer", "john@example.com");
        when(employeeService.getEmployeeDetailsById(id)).thenReturn(mockEmployee);
        ResponseEntity<Employee> response = employeeController.getEmployeeById(id);
        assertEquals(mockEmployee, response.getBody());
        assertEquals(200, response.getStatusCode().value());
        verify(employeeService, times(1)).getEmployeeDetailsById(id);
    }

    @Test
    void getHighestSalaryOfEmployees_shouldReturnHighestSalary() {
        Integer mockSalary = 5000;
        when(employeeService.getHighestSalaryOfEmployees()).thenReturn(mockSalary);
        ResponseEntity<Integer> response = employeeController.getHighestSalaryOfEmployees();
        assertEquals(mockSalary, response.getBody());
        assertEquals(200, response.getStatusCode().value());
        verify(employeeService, times(1)).getHighestSalaryOfEmployees();
    }

    @Test
    void getTopTenHighestEarningEmployeeNames_shouldReturnTopTenEmployeeNames() {
        List<String> mockNames = Arrays.asList("John Doe", "Jane Doe");
        when(employeeService.getTopTenHighestEarningEmployeeNames()).thenReturn(mockNames);
        ResponseEntity<List<String>> response = employeeController.getTopTenHighestEarningEmployeeNames();
        assertEquals(mockNames, response.getBody());
        assertEquals(200, response.getStatusCode().value());
        verify(employeeService, times(1)).getTopTenHighestEarningEmployeeNames();
    }

    @Test
    void createEmployee_shouldReturnCreatedEmployee() {
        Map<String, Object> employeeInput = Map.of("name", "John Doe", "salary", 5000);
        Employee mockEmployee = new Employee(UUID.fromString("4ea6f6dc-eb53-42b8-a6a5-f3fde7ab803a"), "John Doe", 5000, 25, "Engineer", "john@example.com");
        when(employeeService.createEmployee(employeeInput)).thenReturn(mockEmployee);
        ResponseEntity<Employee> response = employeeController.createEmployee(employeeInput);
        assertEquals(mockEmployee, response.getBody());
        assertEquals(201, response.getStatusCode().value());
        verify(employeeService, times(1)).createEmployee(employeeInput);
    }

    @Test
    void deleteEmployeeById_shouldReturnDeletionMessage() {
        String id = "4ea6f6dc-eb53-42b8-a6a5-f3fde7ab803a";
        String mockResponse = "Employee deleted successfully";
        when(employeeService.deleteEmployeeById(id)).thenReturn(mockResponse);
        ResponseEntity<String> response = employeeController.deleteEmployeeById(id);
        assertEquals(mockResponse, response.getBody());
        assertEquals(200, response.getStatusCode().value());
        verify(employeeService, times(1)).deleteEmployeeById(id);
    }
}