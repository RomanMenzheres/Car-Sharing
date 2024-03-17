package com.example.carsharing.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.carsharing.dto.rental.CreateRentalRequestDto;
import com.example.carsharing.dto.rental.RentalDto;
import com.example.carsharing.dto.rental.RentalWithDetailedCarInfoDto;
import com.example.carsharing.supplier.RentalSupplier;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
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
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RentalControllerTest {
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
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("db/rental/add-default-rentals.sql")
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
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("db/rental/delete-all-rentals.sql")
            );
        }
    }

    @WithUserDetails(value = "john.doe@example.com",
            userDetailsServiceBeanName = "customUserDetailsService")
    @Test
    @Sql(
            scripts = "classpath:db/rental/delete-created-rental.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    @DisplayName("Create a new rental with valid request dto test")
    void createRental_ValidRequestDto_Success() throws Exception {
        CreateRentalRequestDto requestDto = RentalSupplier.getCreateRentalRequestDto();
        RentalWithDetailedCarInfoDto expected = RentalSupplier.getRentalWithDetailedCarInfoDto();

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(
                        post("/rentals")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        RentalWithDetailedCarInfoDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), RentalWithDetailedCarInfoDto.class
        );

        assertNotNull(actual);
        assertNotNull(actual.id());
        EqualsBuilder.reflectionEquals(expected, actual, "id");
    }

    @WithUserDetails(value = "john.doe@example.com",
            userDetailsServiceBeanName = "customUserDetailsService")
    @Test
    @DisplayName("Create a new rental with invalid request dto test")
    void createRental_InvalidRequestDto_BadRequest() throws Exception {
        CreateRentalRequestDto requestDto = RentalSupplier.getInvalidCreateRentalRequestDto();

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(
                        post("/rentals")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @WithUserDetails(value = "john.doe@example.com",
            userDetailsServiceBeanName = "customUserDetailsService")
    @Test
    @DisplayName("Get all user's rentals test")
    void getMyRentals_GivenRentals_Success() throws Exception {
        List<RentalDto> expected = RentalSupplier.getAllRentalsByUserWithId1();

        MvcResult result = mockMvc.perform(
                        get("/rentals/my")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        RentalDto[] actual = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(), RentalDto[].class);
        System.out.println(Arrays.toString(actual));
        assertEquals(expected.size(), actual.length);
        assertEquals(expected, Arrays.stream(actual).toList());
    }

    @WithMockUser(username = "admin", authorities = {"MANAGER"})
    @Test
    @DisplayName("Get rental by valid id test")
    void getRentalById_ValidId_Success() throws Exception {
        RentalWithDetailedCarInfoDto expected = RentalSupplier.getCarWithId2();

        MvcResult result = mockMvc.perform(
                        get("/rentals/2")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        RentalWithDetailedCarInfoDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), RentalWithDetailedCarInfoDto.class);

        assertNotNull(actual);
        EqualsBuilder.reflectionEquals(expected, actual);
    }

    @WithMockUser(username = "admin", authorities = {"MANAGER"})
    @Test
    @DisplayName("Get rental by invalid id test")
    void getRentalsById_NotExistsId_NotFound() throws Exception {
        mockMvc.perform(
                        get("/rentals/-1")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @WithMockUser(username = "admin", authorities = {"MANAGER"})
    @Test
    @DisplayName("Get all rentals by user and activity test")
    void getRentalsByUserAndActivity_GivenRentals_Success() throws Exception {
        List<RentalDto> expected = RentalSupplier.getAllActiveRentalsByUserWithId1();

        MvcResult result = mockMvc.perform(
                        get("/rentals?user_id=1&is_active=true")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        RentalDto[] actual = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(), RentalDto[].class);

        assertEquals(expected.size(), actual.length);
        assertEquals(expected, Arrays.stream(actual).toList());
    }

    @WithMockUser(username = "admin", authorities = {"MANAGER"})
    @Test
    @DisplayName("Get all rentals by user and activity test")
    void getRentalsByUserAndActivity_UserIdIsNull_Success() throws Exception {
        List<RentalDto> expected = RentalSupplier.getAllActiveRentals();

        MvcResult result = mockMvc.perform(
                        get("/rentals?is_active=true")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        RentalDto[] actual = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(), RentalDto[].class);

        assertEquals(expected.size(), actual.length);
        assertEquals(expected, Arrays.stream(actual).toList());
    }
}
