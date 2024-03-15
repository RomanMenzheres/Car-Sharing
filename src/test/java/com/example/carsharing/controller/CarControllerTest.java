package com.example.carsharing.controller;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.carsharing.dto.car.CarDto;
import com.example.carsharing.dto.car.CreateCarRequestDto;
import com.example.carsharing.supplier.CarSupplier;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CarControllerTest {
    protected static MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(
            @Autowired DataSource dataSource,
            @Autowired WebApplicationContext applicationContext
    ) throws SQLException {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
        teardown(dataSource);
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("db/car/add-default-cars.sql")
            );
        }
    }

    @AfterAll
    static void afterAll(@Autowired DataSource dataSource) {
        teardown(dataSource);
    }

    @SneakyThrows
    static void teardown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("db/car/delete-all-cars.sql")
            );
        }
    }

    @WithMockUser(username = "admin", authorities = {"MANAGER"})
    @Test
    @Sql(
            scripts = "classpath:db/car/delete-created-car.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    @DisplayName("Create a new car with valid request dto test")
    void createCar_ValidRequestDto_Success() throws Exception {
        CreateCarRequestDto requestDto = CarSupplier.getCreateCarRequestDto();
        CarDto expected = CarSupplier.getCarDto();

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(
                        post("/cars")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        CarDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), CarDto.class
        );

        assertNotNull(actual);
        assertNotNull(actual.id());
        EqualsBuilder.reflectionEquals(expected, actual, "id");
    }

    @WithMockUser(username = "admin", authorities = {"MANAGER"})
    @Test
    @DisplayName("Create a new car with invalid request dto test")
    void createCar_InvalidRequestDto_BadRequest() throws Exception {
        CreateCarRequestDto requestDto = CarSupplier.getInvalidCreateCarRequestDto();

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(
                        post("/cars")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @WithMockUser(username = "admin", authorities = {"MANAGER"})
    @Test
    @DisplayName("Get car by valid id test")
    void getCarById_ValidId_Success() throws Exception {
        CarDto expected = CarSupplier.getCarWithId2();

        MvcResult result = mockMvc.perform(
                        get("/cars/2")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        CarDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), CarDto.class);

        assertNotNull(actual);
        EqualsBuilder.reflectionEquals(expected, actual);
    }

    @WithMockUser(username = "admin", authorities = {"MANAGER"})
    @Test
    @DisplayName("Get car by invalid id test")
    void getCarById_NotExistsId_NotFound() throws Exception {
        mockMvc.perform(
                        get("/cars/-1")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @WithMockUser(username = "admin", authorities = {"MANAGER"})
    @Test
    @Sql(
            scripts = "classpath:db/car/rollback-updated-car.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    @DisplayName("Update a new car with valid request dto test")
    void updateCar_ValidRequestDto_Success() throws Exception {
        CreateCarRequestDto requestDto = CarSupplier.getUpdatedCreateCarRequestDto();
        CarDto expected = CarSupplier.getUpdatedCarDto();

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(
                        put("/cars/2")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        CarDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), CarDto.class
        );

        assertNotNull(actual);
        assertNotNull(actual.id());
        EqualsBuilder.reflectionEquals(expected, actual);
    }

    @WithMockUser(username = "admin", authorities = {"MANAGER"})
    @Test
    @DisplayName("Update a new car with invalid request dto test")
    void updateCar_InvalidRequestDto_BadRequest() throws Exception {
        CreateCarRequestDto requestDto = CarSupplier.getInvalidCreateCarRequestDto();

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(
                        put("/cars/2")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @WithMockUser(username = "admin", authorities = {"MANAGER"})
    @Test
    @DisplayName("Update a new car by invalid car id test")
    void updateCar_InvalidCarId_NotFound() throws Exception {
        CreateCarRequestDto requestDto = CarSupplier.getUpdatedCreateCarRequestDto();

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(
                        put("/cars/-1")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();
    }
}
