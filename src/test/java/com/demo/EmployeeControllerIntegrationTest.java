package com.demo;

import com.demo.DemoApplication;
import com.demo.base.BaseResponse;
import com.demo.company.controller.EmployeeControllerPath;
import com.demo.company.entity.Employee;
import com.demo.company.repository.EmployeeRepository;
import com.demo.dto.DepartmentRequest;
import com.demo.dto.EmployeeCreateRequest;
import com.demo.dto.EmployeeUpdateRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.ValidatableResponse;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.TextIndexDefinition;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.bind.annotation.RequestParam;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DemoApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EmployeeControllerIntegrationTest {
    public static final String STORE_ID_KEY = "storeId";
    public static final String CHANNEL_ID_KEY = "channelId";
    public static final String CLIENT_ID = "clientId";
    public static final String REQUEST_ID_KEY = "requestId";
    public static final String USERNAME_KEY = "username";
    public static final String DEPT_NAME = "Haha";
    public static final int DEPT_NO = 27;
    public static final String LOC = "Jalan jalan";
    public static final int EMP_NO = 1;
    public static final String EMP_NAME = "Test";
    public static final double COMM = 10.0;
    public static final String JOB = "seller";
    // public static final String HIRE_DATE = "2020-02-02";
    public static final int MGR = 5;
    public static final double SAL = 50.0;
    public static final String STORE_ID_VALUE = "1";
    public static final String CHANNEL_ID_VALUE = "2";
    public static final String CLIENT_ID_VALUE = "3";
    public static final String REQUEST_ID_VALUE = "4";
    public static final String USERNAME_VALUE = "SYSTEM";
    public static final String CONTEXT_PATH = "/demo";
    private ObjectMapper objectMapper;

    @Value("${local.server.port}")
    private int port;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    // Jalanin rest assured dengan port yang ditentukan
    @Before
    public void setUp() throws Exception {
        RestAssured.port = port;
        objectMapper = new ObjectMapper();
        employeeRepository.deleteAll();
//    mongoTemplate.indexOps(Employee.class).ensureIndex(TextIndexDefinition.builder().onField("empNo").build());
        mongoTemplate.indexOps(Employee.class).ensureIndex(new Index("empNo", Sort.Direction.ASC).unique());
    }

    @Test
    public void createEmployee_success_returnBaseResponse() throws Exception {
        EmployeeCreateRequest request;
        DepartmentRequest departmentRequest;
        departmentRequest =
                DepartmentRequest.builder().deptName(DEPT_NAME).deptNo(DEPT_NO).loc(LOC).build();
        request = EmployeeCreateRequest.builder().empNo(EMP_NO).empName(EMP_NAME).comm(COMM)
                .department(departmentRequest).job(JOB).mgr(MGR).sal(SAL).build();

        ValidatableResponse validatableResponse =
                RestAssured.given().contentType("application/json").queryParam(STORE_ID_KEY, STORE_ID_VALUE)
                        .queryParam(CHANNEL_ID_KEY, CHANNEL_ID_VALUE).queryParam(CLIENT_ID, CLIENT_ID_VALUE)
                        .queryParam(REQUEST_ID_KEY, REQUEST_ID_VALUE).queryParam(USERNAME_KEY, USERNAME_VALUE)
                        .body(request).post(CONTEXT_PATH + EmployeeControllerPath.BASE_PATH).then();
        // .body(objectMapper.writeValueAsString(request));

        BaseResponse baseResponse =
                objectMapper.readValue(validatableResponse.extract().asString(), BaseResponse.class);
        Assert.assertTrue(baseResponse.isSuccess());
        Employee employee = employeeRepository.findFirstByEmpNoAndMarkForDeleteFalse(EMP_NO);
        Assert.assertEquals(EMP_NAME, employee.getEmpName());
    }

    @Test
    public void createEmployee_failed_returnBaseResponse() throws Exception {
        EmployeeCreateRequest request;
        DepartmentRequest departmentRequest;
        departmentRequest =
                DepartmentRequest.builder().deptName(DEPT_NAME).deptNo(DEPT_NO).loc(LOC).build();
        request = EmployeeCreateRequest.builder().empNo(EMP_NO).empName(EMP_NAME).comm(COMM)
                .department(departmentRequest).job(JOB).mgr(MGR).sal(SAL).build();

        Employee employee = Employee.builder().empNo(EMP_NO).empName(EMP_NAME).comm(COMM)
                .job(JOB).mgr(MGR).sal(SAL).build();

        employeeRepository.save(employee);
        ValidatableResponse validatableResponse =
                RestAssured.given().contentType("application/json").queryParam(STORE_ID_KEY, STORE_ID_VALUE)
                        .queryParam(CHANNEL_ID_KEY, CHANNEL_ID_VALUE).queryParam(CLIENT_ID, CLIENT_ID_VALUE)
                        .queryParam(REQUEST_ID_KEY, REQUEST_ID_VALUE).queryParam(USERNAME_KEY, USERNAME_VALUE)
                        .body(request).post(CONTEXT_PATH + EmployeeControllerPath.BASE_PATH).then();

//        System.out.println(validatableResponse.extract().asString());
        BaseResponse baseResponse =
                objectMapper.readValue(validatableResponse.extract().asString(), BaseResponse.class);
        Assert.assertFalse(baseResponse.isSuccess());
    }

    @Test
    public void updateEmployeeByEmpNo_success_returnBaseResponse() throws Exception {
        EmployeeUpdateRequest request;
        DepartmentRequest departmentRequest;
        departmentRequest =
                DepartmentRequest.builder().deptName(DEPT_NAME).deptNo(DEPT_NO).loc(LOC).build();
        request = EmployeeUpdateRequest.builder().empName(EMP_NAME).comm(COMM)
                .department(departmentRequest).job(JOB).mgr(MGR).sal(SAL).build();
        Employee employee = Employee.builder().empNo(EMP_NO).empName(EMP_NAME).comm(COMM)
                                .job(JOB).mgr(MGR).sal(SAL).build();

        employeeRepository.save(employee);
        ValidatableResponse validatableResponse =
                RestAssured.given().contentType("application/json").queryParam(STORE_ID_KEY, STORE_ID_VALUE)
                        .queryParam(CHANNEL_ID_KEY, CHANNEL_ID_VALUE).queryParam(CLIENT_ID, CLIENT_ID_VALUE)
                        .queryParam(REQUEST_ID_KEY, REQUEST_ID_VALUE).queryParam(USERNAME_KEY, USERNAME_VALUE)
                        .body(request).put(CONTEXT_PATH + EmployeeControllerPath.BASE_PATH + "/" + EMP_NO ).then();

//        System.out.println(validatableResponse.extract().asString());
        BaseResponse baseResponse =
                objectMapper.readValue(validatableResponse.extract().asString(), BaseResponse.class);
        Assert.assertTrue(baseResponse.isSuccess());
        employee = employeeRepository.findFirstByEmpNoAndMarkForDeleteFalse(EMP_NO);
        Assert.assertTrue(employee.getDepartment() != null);
    }

}