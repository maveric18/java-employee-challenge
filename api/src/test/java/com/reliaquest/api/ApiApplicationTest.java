package com.reliaquest.api;

import com.reliaquest.api.dto.*;
import com.reliaquest.api.exception.EmployeeDataNotFoundException;
import com.reliaquest.api.helper.EmployeeHelper;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.repository.EmployeeRepository;
import com.reliaquest.api.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

import static com.reliaquest.api.constant.EmployeeConstant.BASE_URL;
import static com.reliaquest.api.constant.EmployeeConstant.FETCH_EMPLOYEE_DETAILS_BY_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
class RqChallengeApplicationTests {

    private RestTemplate restTemplate;
    private EmployeeRepository employeeRepository;
    private EmployeeHelper employeeHelper;
    private EmployeeService employeeService;

    @BeforeEach
    public void setup() {
        restTemplate = mock(RestTemplate.class);
        employeeRepository = mock(EmployeeRepository.class);
        employeeHelper = new EmployeeHelper(restTemplate, employeeRepository);
        employeeService = new EmployeeService(employeeHelper);
    }

    public List<EmployeeModel> getMockListOfEmp() {
        return Arrays.asList(
                new EmployeeModel(UUID.fromString("4ea6f6dc-eb53-42b8-a6a5-f3fde7ab803a"), "Dhiraj", 4500, 23, "Design Engineer", "spatil@company.com"),
                new EmployeeModel(UUID.fromString("4ea6f6dc-eb53-42b8-a6a5-f3fde7ab803b"), "Suraj", 5500, 26, "", ""),
                new EmployeeModel(UUID.fromString("4ea6f6dc-eb53-42b8-a6a5-f3fde7ab803c"), "Rajesh", 4100, 22, "", ""),
                new EmployeeModel(UUID.fromString("4ea6f6dc-eb53-42b8-a6a5-f3fde7ab803d"), "Ramesh", 4500, 23, "", ""),
                new EmployeeModel(UUID.fromString("4ea6f6dc-eb53-42b8-a6a5-f3fde7ab803e"), "Rajendra", 4101, 32, "", ""),
                new EmployeeModel(UUID.fromString("4ea6f6dc-eb53-42b8-a6a5-f3fde7ab803f"), "Pavan", 6600, 31, "", ""),
                new EmployeeModel(UUID.fromString("4ea6f6dc-eb53-42b8-a6a5-f3fde7ab801a"), "Shivam", 7700, 30, "", ""),
                new EmployeeModel(UUID.fromString("4ea6f6dc-eb53-42b8-a6a5-f3fde7ab801b"), "Shivraj", 2700, 50, "", ""),
                new EmployeeModel(UUID.fromString("4ea6f6dc-eb53-42b8-a6a5-f3fde7ab801c"), "Viraj", 6000, 19, "", ""),
                new EmployeeModel(UUID.fromString("4ea6f6dc-eb53-42b8-a6a5-f3fde7ab801d"), "Siraj", 6235, 20, "", ""),
                new EmployeeModel(UUID.fromString("4ea6f6dc-eb53-42b8-a6a5-f3fde7ab801e"), "Virat", 4511, 24, "", ""),
                new EmployeeModel(UUID.fromString("4ea6f6dc-eb53-42b8-a6a5-f3fde7ab801f"), "Rohit", 8400, 23, "", ""),
                new EmployeeModel(UUID.fromString("4ea6f6dc-eb53-42b8-a6a5-f3fde7ab802a"), "Rishabh", 900, 25, "", "")
        );
    }

    private HttpEntity getEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(headers);
    }

    @Test
    void contextLoads() {
    }

    @Test
    public void testFetchAllEmployeeDataTest() throws Exception {
        List<EmployeeModel> mockEmployees = getMockListOfEmp();
        EmployeeListResponse employeeListResponse = new EmployeeListResponse();
        employeeListResponse.setData(mockEmployees);
        employeeListResponse.setStatus("success");
        ResponseEntity<EmployeeListResponse> mockResponse = ResponseEntity.ok(employeeListResponse);
        when(restTemplate.exchange(BASE_URL, HttpMethod.GET, getEntity(), EmployeeListResponse.class)).thenReturn(mockResponse);
        List<Employee> employeeList = mockEmployees.stream().map(EmployeeModel::convertEmployeeModelToEmployee).collect(Collectors.toList());
        when(employeeRepository.saveAll(Mockito.<List<Employee>>any()))
                .thenReturn(employeeList);
        List<Employee> result = employeeService.getAllEmployees();
        assertEquals(mockEmployees.size(), result.size());
        assertEquals("success", employeeListResponse.getStatus());
    }

    @Test
    public void testFetchAllEmployeeData_emptyResponse() {
        EmployeeListResponse mockResponse = new EmployeeListResponse();
        mockResponse.setData(Collections.emptyList());
        ResponseEntity<EmployeeListResponse> mockResponseEntity = ResponseEntity.ok(mockResponse);
        when(restTemplate.exchange(BASE_URL, HttpMethod.GET, getEntity(), EmployeeListResponse.class))
                .thenReturn(mockResponseEntity);
        EmployeeDataNotFoundException exception = assertThrows(EmployeeDataNotFoundException.class,
                () -> employeeService.getAllEmployees());
        assertEquals("Data Not Found", exception.getMessage());
    }

    @Test
    void testFetchAllEmployeeData_GeneralException() {
        when(restTemplate.exchange(anyString(), any(), any(), eq(EmployeeListResponse.class)))
                .thenThrow(new RuntimeException("General exception"));
        Exception exception = assertThrows(Exception.class, () -> {
            employeeHelper.fetchAllEmployeeData();
        });
        assertEquals("General exception", exception.getMessage());
        verify(restTemplate, times(1)).exchange(anyString(), any(), any(), eq(EmployeeListResponse.class));
    }


    public ResponseEntity<EmployeeListResponse> getMockedResponseEntity(List<EmployeeModel> employeeModelList) {
        EmployeeListResponse employeeListResponse = new EmployeeListResponse();
        employeeListResponse.setData(employeeModelList);
        return new ResponseEntity<>(employeeListResponse, HttpStatus.OK);
    }

    @Test
    public void filterEmpNameFromSearchStringTest() throws Exception {
        String searchString = "raj";
        List<EmployeeModel> employeeModelList = getMockListOfEmp();
        ResponseEntity<EmployeeListResponse> mockResponseEntity = getMockedResponseEntity(employeeModelList);
        List<Employee> employeeList = employeeModelList.stream()
                .map(EmployeeModel::convertEmployeeModelToEmployee)
                .collect(Collectors.toList());
        List<Employee> filterListMockResponse = employeeList.stream()
                .filter(employee -> employee.getName().toLowerCase().contains(searchString.toLowerCase()))
                .collect(Collectors.toList());
        when(restTemplate.exchange(BASE_URL, HttpMethod.GET, getEntity(), EmployeeListResponse.class))
                .thenReturn(mockResponseEntity);
        List<Employee> actualResponse = employeeService.filterEmpNameFromSearchString(searchString);
        assertEquals(filterListMockResponse.size(), actualResponse.size());
    }

    @Test
    public void filterEmpNameFromSearchStringTest_emptyResponse() {
        String searchString = "raj";
        EmployeeListResponse mockResponse = new EmployeeListResponse();
        mockResponse.setData(Collections.emptyList());
        ResponseEntity<EmployeeListResponse> mockResponseEntity = ResponseEntity.ok(mockResponse);
        when(restTemplate.exchange(BASE_URL, HttpMethod.GET, getEntity(), EmployeeListResponse.class))
                .thenReturn(mockResponseEntity);
        EmployeeDataNotFoundException exception = assertThrows(EmployeeDataNotFoundException.class,
                () -> employeeService.filterEmpNameFromSearchString(searchString));
        assertEquals("Data Not Found", exception.getMessage());
    }

    @Test
    public void filterEmpNameFromSearchStringTest_GenericException() {
        when(restTemplate.exchange(BASE_URL, HttpMethod.GET, getEntity(), EmployeeListResponse.class))
                .thenThrow(new RuntimeException("unknown Exception"));
        assertThrows(RuntimeException.class, () -> employeeService.filterEmpNameFromSearchString("raj"));
    }

    @Test
    public void getHighestSalaryOfEmployeesTest() throws Exception {
        List<EmployeeModel> employeeModelList = getMockListOfEmp();
        ResponseEntity<EmployeeListResponse> mockResponseEntity = getMockedResponseEntity(employeeModelList);
        List<Employee> employeeList = employeeModelList.stream()
                .map(EmployeeModel::convertEmployeeModelToEmployee)
                .collect(Collectors.toList());
        Integer mockHighestSal = employeeList.stream().mapToInt(Employee::getSalary).max().getAsInt();
        when(restTemplate.exchange(BASE_URL, HttpMethod.GET, getEntity(), EmployeeListResponse.class))
                .thenReturn(mockResponseEntity);
        Integer actualHighestSal = employeeService.getHighestSalaryOfEmployees();
        assertEquals(mockHighestSal, actualHighestSal);
    }

    @Test
    public void getHighestSalaryOfEmployeesTest_emptyResponse() {
        EmployeeListResponse mockResponse = new EmployeeListResponse();
        mockResponse.setData(Collections.emptyList());
        ResponseEntity<EmployeeListResponse> mockResponseEntity = ResponseEntity.ok(mockResponse);
        when(restTemplate.exchange(BASE_URL, HttpMethod.GET, getEntity(), EmployeeListResponse.class))
                .thenReturn(mockResponseEntity);
        EmployeeDataNotFoundException exception = assertThrows(EmployeeDataNotFoundException.class,
                () -> employeeService.getHighestSalaryOfEmployees());

        assertEquals("Data Not Found", exception.getMessage());
    }

    @Test
    public void getHighestSalaryOfEmployeesTest_GenericException() {
        when(restTemplate.exchange(BASE_URL, HttpMethod.GET, getEntity(), EmployeeListResponse.class))
                .thenThrow(new RuntimeException("unknown Exception"));
        assertThrows(RuntimeException.class, () -> employeeService.getHighestSalaryOfEmployees());
    }

    @Test
    public void getTopTenHighestEarningEmployeeNamesTest() {
        List<EmployeeModel> employeeModelList = getMockListOfEmp();
        ResponseEntity<EmployeeListResponse> mockResponseEntity = getMockedResponseEntity(employeeModelList);
        List<Employee> employeeList = employeeModelList.stream()
                .map(EmployeeModel::convertEmployeeModelToEmployee)
                .collect(Collectors.toList());
        List<String> mockEmpNameList = employeeList.stream()
                .sorted(Comparator.comparingInt(Employee::getSalary).reversed())
                .limit(10)
                .map(Employee::getName)
                .collect(Collectors.toList());
        when(restTemplate.exchange(BASE_URL, HttpMethod.GET, getEntity(), EmployeeListResponse.class))
                .thenReturn(mockResponseEntity);
        List<String> actualEmpNameList = employeeService.getTopTenHighestEarningEmployeeNames();
        assertEquals(mockEmpNameList, actualEmpNameList);
    }

    @Test
    public void getTopTenHighestEarningEmployeeNamesTest_emptyResponse() {
        EmployeeListResponse mockResponse = new EmployeeListResponse();
        mockResponse.setData(Collections.emptyList());
        ResponseEntity<EmployeeListResponse> mockResponseEntity = ResponseEntity.ok(mockResponse);
        when(restTemplate.exchange(BASE_URL, HttpMethod.GET, getEntity(), EmployeeListResponse.class))
                .thenReturn(mockResponseEntity);
        EmployeeDataNotFoundException exception = assertThrows(EmployeeDataNotFoundException.class,
                () -> employeeService.getTopTenHighestEarningEmployeeNames());
        assertEquals("Data Not Found", exception.getMessage());
    }

    @Test
    public void getTopTenHighestEarningEmployeeNamesTest_GenericException() {

        when(restTemplate.exchange(BASE_URL, HttpMethod.GET, getEntity(), EmployeeListResponse.class))
                .thenThrow(new RuntimeException("unknown Exception"));
        assertThrows(RuntimeException.class, () -> employeeService.getTopTenHighestEarningEmployeeNames());
    }

    @Test
    public void getEmployeeDetailsByIdTest() {
        EmployeeModel employeeModel = new EmployeeModel(UUID.fromString("4ea6f6dc-eb53-42b8-a6a5-f3fde7ab803a"), "Dhiraj", 4512, 23, "", "");
        Employee mockedEmployee = EmployeeModel.convertEmployeeModelToEmployee(employeeModel);
        EmployeeResponse employeeResponse = new EmployeeResponse();
        employeeResponse.setData(employeeModel);
        employeeResponse.setStatus("success");
        ResponseEntity<EmployeeResponse> mockedResponse = new ResponseEntity(employeeResponse, HttpStatus.OK);
        when(restTemplate.exchange(BASE_URL + FETCH_EMPLOYEE_DETAILS_BY_ID + employeeModel.getId(), HttpMethod.GET, getEntity(), EmployeeResponse.class))
                .thenReturn(mockedResponse);
        Employee actualEmployee = employeeService.getEmployeeDetailsById("4ea6f6dc-eb53-42b8-a6a5-f3fde7ab803a");
        assertEquals(mockedEmployee, actualEmployee);
        assertEquals("success", employeeResponse.getStatus());
    }

    @Test
    public void getEmployeeDetailsByIdTest_illegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> employeeService.getEmployeeDetailsById(""));
        assertEquals("Data should not empty or null", exception.getMessage());
    }

    @Test
    public void getEmployeeDetailsByIdTest_emptyResponse() {
        ResponseEntity<EmployeeResponse> mockedResponse = new ResponseEntity(HttpStatus.OK);
        when(restTemplate.exchange(BASE_URL + FETCH_EMPLOYEE_DETAILS_BY_ID + "4ea6f6dc-eb53-42b8-a6a5-f3fde7ab202a", HttpMethod.GET, getEntity(), EmployeeResponse.class))
                .thenReturn(mockedResponse);
        EmployeeDataNotFoundException exception = assertThrows(EmployeeDataNotFoundException.class,
                () -> employeeService.getEmployeeDetailsById("4ea6f6dc-eb53-42b8-a6a5-f3fde7ab202a"));
        assertEquals("Data Not Found", exception.getMessage());
    }

    @Test
    public void getEmployeeDetailsByIdTest_GenericException() {
        when(restTemplate.exchange(BASE_URL + FETCH_EMPLOYEE_DETAILS_BY_ID + "4ea6f6dc-eb53-42b8-a6a5-f3fde7ab803a", HttpMethod.GET, getEntity(), EmployeeListResponse.class))
                .thenThrow(new RuntimeException("unknown Exception"));
        assertThrows(RuntimeException.class, () -> employeeService.getEmployeeDetailsById("4ea6f6dc-eb53-42b8-a6a5-f3fde7ab803a"));
    }

    @Test
    public void deleteEmployeeDetailsByIdTest() {

        EmployeeDeleteResponse employeeDeleteResponse = new EmployeeDeleteResponse();
        employeeDeleteResponse.setMessage("successfully! deleted Record");
        employeeDeleteResponse.setStatus("success");
        ResponseEntity<EmployeeDeleteResponse> mockedResponse = new ResponseEntity(employeeDeleteResponse, HttpStatus.OK);
        when(restTemplate.exchange(BASE_URL + "/" + "4ea6f6dc-eb53-42b8-a6a5-f3fde7ab803a", HttpMethod.DELETE, getEntity(), EmployeeDeleteResponse.class))
                .thenReturn(mockedResponse);
        String actualResponse = employeeService.deleteEmployeeById("4ea6f6dc-eb53-42b8-a6a5-f3fde7ab803a");
        assertEquals(actualResponse, "successfully! deleted Record");
        assertEquals("success", employeeDeleteResponse.getStatus());
    }

    @Test
    public void deleteEmployeeDetailsByIdTest_illegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> employeeService.deleteEmployeeById(""));
        assertEquals("Data should not empty or null", exception.getMessage());
    }

    @Test
    public void deleteEmployee_GenericException() {
        when(restTemplate.exchange(BASE_URL + "/" + UUID.randomUUID(), HttpMethod.DELETE, getEntity(), EmployeeDeleteResponse.class))
                .thenThrow(new RuntimeException("unknown Exception"));
        assertThrows(RuntimeException.class, () -> employeeService.deleteEmployeeById("4ea6f6dc-eb53-42b8-a6a5-f3fde7ab803a"));
    }

    @Test
    public void createEmployee() {
        Map<String, Object> data = new HashMap() {{
            put("name", "Dhiraj");
            put("salary", "4512");
            put("age", "23");
            put("title", "Design Engineer");
            put("email", "spatil@company.com");
        }};
        Employee mockedEmployee = new Employee(UUID.fromString("4ea6f6dc-eb53-42b8-a6a5-f3fde7ab803a"), "Dhiraj", 4545, 23, "", "dhiraj@company.com");
        EmployeeCreateResponse employeeCreateResponse = new EmployeeCreateResponse();
        employeeCreateResponse.setData(mockedEmployee);
        employeeCreateResponse.setStatus("success");
        employeeCreateResponse.setMessage("create employee success");
        ResponseEntity<EmployeeCreateResponse> mockedResponse = new ResponseEntity(employeeCreateResponse, HttpStatus.OK);
        when(restTemplate.exchange(BASE_URL, HttpMethod.POST, new HttpEntity<>(data), EmployeeCreateResponse.class))
                .thenReturn(mockedResponse);
        when(employeeRepository.save(mockedEmployee)).thenReturn(mockedEmployee);
        Employee actualEmployee = employeeService.createEmployee(data);
        assertEquals(actualEmployee, mockedEmployee);
        assertEquals("success", employeeCreateResponse.getStatus());
    }

    @Test
    public void createEmployee_illegalArgumentException() {
        Map<String, Object> data = new HashMap() {{
            put("name", "Dhiraj");
            put("salary", "4512");
        }};
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> employeeService.createEmployee(data));
        assertEquals("Incomplete data provided, please provide name, age, salary", exception.getMessage());
    }

    @Test
    public void createEmployee_illegalArgumentException2() {
        Map<String, Object> data = new HashMap() {{
            put("name", "Dhiraj");
            put("salary", "4512");
            put("age", "-45");
        }};
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> employeeService.createEmployee(data));
        assertEquals("Invalid data provided for field age and salary", exception.getMessage());
    }

    @Test
    public void createEmployee_numberFormatException() {
        Map<String, Object> data = new HashMap() {{
            put("name", "Dhiraj");
            put("salary", "dhiraj");
            put("age", "23");
        }};
        NumberFormatException exception = assertThrows(NumberFormatException.class,
                () -> employeeService.createEmployee(data));
        assertEquals("Invalid data provided for age and salary, please provide valid integer", exception.getMessage());
    }

    @Test
    public void testCreateEmployee_GenericException() {
        Map<String, Object> data = new HashMap() {{
            put("name", "Dhiraj");
            put("salary", "4545");
            put("age", "23");
        }};
        when(restTemplate.exchange(BASE_URL, HttpMethod.POST, new HttpEntity<>(data), EmployeeCreateResponse.class))
                .thenThrow(new RuntimeException("unknown Exception"));
        assertThrows(RuntimeException.class, () -> employeeService.createEmployee(data));
    }
}