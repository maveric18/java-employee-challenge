package com.reliaquest.api.helper;

import com.reliaquest.api.dto.*;
import com.reliaquest.api.exception.EmployeeDataNotFoundException;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.repository.EmployeeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

import static com.reliaquest.api.constant.EmployeeConstant.*;

@Component
@Slf4j
public class EmployeeHelper {

    private EmployeeRepository employeeRepository;
    private RestTemplate restTemplate;

    @Autowired
    public EmployeeHelper(RestTemplate restTemplate, EmployeeRepository employeeRepository) {
        this.restTemplate = restTemplate;
        this.employeeRepository = employeeRepository;
    }

    @PostConstruct
    public void init() {
        fetchAllEmployeeData();
    }


    private HttpEntity getEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(headers);
    }

    public List<Employee> fetchAllEmployeeData() {
        try {
            log.info("Fetching the employee list from the external API and saving the response in cache db");
            ResponseEntity<EmployeeListResponse> response = restTemplate
                    .exchange(BASE_URL, HttpMethod.GET, getEntity(), EmployeeListResponse.class);
            EmployeeListResponse employeeListResponse = response.getBody();
            if (employeeListResponse == null || CollectionUtils.isEmpty(employeeListResponse.getData())) {
                throw new EmployeeDataNotFoundException("Data Not Found");
            }
            log.info("converting employee model class to employee entity class");
            List<Employee> employeeList = employeeListResponse.getData().stream()
                    .map(EmployeeModel::convertEmployeeModelToEmployee)
                    .collect(Collectors.toList());
            saveEmployeeListIntoLocalDb(employeeList);
            return employeeList;
        } catch (EmployeeDataNotFoundException ee) {
            log.error("Employee details not found", ee);
            throw ee;
        } catch (Exception e) {
            log.error("Error occurred while fetching data from the external API", e);
            throw e;
        }
    }

    private void saveEmployeeListIntoLocalDb(List<Employee> employeeList) {
        try {
            log.info("Saving the employee list in in-cache db");
            employeeRepository.saveAll(employeeList);
        } catch (Exception e) {
            log.error("Error occurred while saving the employee list into in-cache db ", e);
        }
    }

    public Employee fetchEmployeeDetailsById(UUID id) {
        try {
            String url = BASE_URL + FETCH_EMPLOYEE_DETAILS_BY_ID + id;
            ResponseEntity<EmployeeResponse> response = restTemplate
                    .exchange(url, HttpMethod.GET, getEntity(), EmployeeResponse.class);
            EmployeeResponse employeeResponse = response.getBody();
            if (employeeResponse == null || employeeResponse.getData() == null) {
                throw new EmployeeDataNotFoundException("Data Not Found");
            }
            log.info("Data for id {} found on external API", id);
            Employee employee = EmployeeModel.convertEmployeeModelToEmployee(employeeResponse.getData());
            saveEmployeeIntoLocalDb(employee);
            return employee;
        } catch (EmployeeDataNotFoundException ee) {
            log.error("Employee details for Id {} not found {}", id, ee);
            throw ee;
        } catch (Exception e) {
            log.error("Error occurred while fetching employee details for Id {} {} ", id, e);
            throw e;
        }
    }

    private void saveEmployeeIntoLocalDb(Employee employee) {
        try {
            employeeRepository.save(employee);
        } catch (Exception e) {
            log.error("Error occurred while saving the employee details for id into the in-cache db", e);
        }
    }

    public String deleteEmployeeDetailsById(UUID id) {
        try {
            String url = BASE_URL + "/" + id;
            ResponseEntity<EmployeeDeleteResponse> response = restTemplate
                    .exchange(url, HttpMethod.DELETE, getEntity(), EmployeeDeleteResponse.class);
            EmployeeDeleteResponse employeeDeleteResponse = response.getBody();
            deleteEmployeeDetailsFromLocalDb(id);
            log.info("Employee details for id {} deleted successfully.", id);
            return employeeDeleteResponse.getMessage();
        } catch (Exception e) {
            log.error("Employee details not deleted for id {}", id, e);
            throw e;
        }
    }

    private void deleteEmployeeDetailsFromLocalDb(UUID id) {
        log.info("Retrieving employee details for employee Id {} from in-cache db", id);
        Optional<Employee> employeeOptional = employeeRepository.findById(id);
        if (employeeOptional.isPresent()) {
            employeeRepository.delete(employeeOptional.get());
        } else {
            log.info("Data not found in-memory cache for id {}", id);
        }
    }

    public Employee createEmployee(Map<String, Object> data) {
        try {
            ResponseEntity<EmployeeCreateResponse> response = restTemplate
                    .exchange(BASE_URL, HttpMethod.POST, new HttpEntity<>(data), EmployeeCreateResponse.class);
            EmployeeCreateResponse employeeCreateResponse = response.getBody();
            Employee employee = employeeCreateResponse.getData();
            saveEmployeeIntoLocalDb(employee);
            log.info("Employee record for Id {} create successfully ..!", employee.getId());
            return employee;
        } catch (Exception e) {
            log.error("Error occurred while storing the employee details into db");
            throw e;
        }
    }
}