package com.reliaquest.api.service;

import com.reliaquest.api.exception.EmployeeDataNotFoundException;
import com.reliaquest.api.helper.EmployeeHelper;
import com.reliaquest.api.model.Employee;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.reliaquest.api.constant.EmployeeConstant.*;


@Service
@Slf4j
public class EmployeeService {

    @Autowired
    EmployeeHelper employeeHelper;

    public EmployeeService(EmployeeHelper employeeHelper) {
        this.employeeHelper = employeeHelper;
    }

    public List<Employee> getAllEmployees() {
        try {
            log.info("Fetching all employees data list");
            return employeeHelper.fetchAllEmployeeData();
        } catch (EmployeeDataNotFoundException ee) {
            throw ee;
        } catch (Exception e) {
            throw e;
        }
    }

    public List<Employee> filterEmpNameFromSearchString(String searchString) {
        try {
            List<Employee> employeeList = employeeHelper.fetchAllEmployeeData();

            log.info("Filtering the employee names according to search string {}", searchString);
            List<Employee> filterNameEmpList = employeeList.stream()
                    .filter(employee -> employee.getName().toLowerCase().contains(searchString.toLowerCase())
                    ).collect(Collectors.toList());
            return filterNameEmpList;
        } catch (EmployeeDataNotFoundException ee) {
            log.error("Data not found in the external API");
            throw ee;
        } catch (Exception e) {
            log.error("Error occurred while filtering employee names based on searchString : {}", searchString);
            throw e;
        }
    }

    public Integer getHighestSalaryOfEmployees() {
        try {
            List<Employee> employeeList = employeeHelper.fetchAllEmployeeData();
            log.info("Fetching highest salary from employee list");
            return employeeList.stream().mapToInt(Employee::getSalary).max().getAsInt();
        } catch (EmployeeDataNotFoundException ee) {
            log.error("Data not found in the external API");
            throw ee;
        } catch (Exception e) {
            log.error("Error occurred while filtering highest salary for emp");
            throw e;
        }
    }

    public List<String> getTopTenHighestEarningEmployeeNames() {
        try {
            List<Employee> employeeList = employeeHelper.fetchAllEmployeeData();
            log.info("Filtering the top-10 highest salary details for employees");
            List<String> empNameList = employeeList.stream()
                    .sorted(Comparator.comparingInt(Employee::getSalary).reversed())
                    .limit(10)
                    .map(Employee::getName)
                    .collect(Collectors.toList());
            return empNameList;
        } catch (EmployeeDataNotFoundException ee) {
            log.error("Data not found in the external API");
            throw ee;
        } catch (Exception e) {
            log.error("Error occurred while retrieving top 10 highest salary name");
            throw e;
        }
    }

    public Employee getEmployeeDetailsById(String id) {
        try {
            if (id == null || id.isEmpty()) {
                throw new IllegalArgumentException("Data should not empty or null");
            }
            log.info("Fetching the data employee details for id: {}", id);
            return employeeHelper.fetchEmployeeDetailsById(UUID.fromString(id));
        } catch (IllegalArgumentException iae) {
            log.error("Invalid data provided", iae);
            throw iae;
        } catch (EmployeeDataNotFoundException ee) {
            throw ee;
        } catch (Exception e) {
            log.error("Error occurred while fetching the data for id {}", id);
            throw e;
        }
    }

    public String deleteEmployeeById(String id) {
        try {
            if (id == null || id.isEmpty()) {
                throw new IllegalArgumentException("Data should not empty or null");
            }
            return employeeHelper.deleteEmployeeDetailsById(UUID.fromString(id));
        } catch (IllegalArgumentException iae) {
            log.error("Invalid data provided", iae);
            throw iae;
        }catch (Exception e) {
            log.error("Error occurred while deleting the data for id {}", id);
            throw e;
        }
    }

    public Employee createEmployee(Map<String, Object> data) {
        try {
            log.info("Saving the employee details for employee {}", data.get("name"));
            validateData(data);
            return employeeHelper.createEmployee(data);
        } catch (NumberFormatException nfe) {
            throw nfe;
        } catch (IllegalArgumentException iae) {
            throw iae;
        } catch (Exception e) {
            throw e;
        }
    }

    private void validateData(Map<String, Object> data) {
        try {
            if (data.containsKey(EMP_NAME) && data.containsKey(EMP_SAL) && data.containsKey(EMP_AGE)
                    && data.get(EMP_NAME) != null && data.get(EMP_SAL) != null && data.get(EMP_AGE) != null) {
                try {
                    int age = Integer.parseInt((String) data.get(EMP_AGE));
                    int salary = Integer.parseInt((String) data.get(EMP_SAL));
                    if (age < 0 || salary < 0) {
                        throw new IllegalArgumentException("Invalid data provided for field age and salary");
                    }
                } catch (NumberFormatException nfe) {
                    log.error("Invalid data provided for age and salary, please provide valid integer", nfe);
                    throw new NumberFormatException("Invalid data provided for age and salary, please provide valid integer");
                }
            } else {
                throw new IllegalArgumentException("Incomplete data provided, please provide name, age, salary");
            }
        } catch (IllegalArgumentException iae) {
            log.error("Error occurred while validating the data, invalid inputs provided", iae);
            throw iae;
        }
    }
}
