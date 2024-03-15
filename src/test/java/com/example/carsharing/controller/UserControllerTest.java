package com.example.carsharing.controller;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.carsharing.dto.user.UserDto;
import com.example.carsharing.dto.user.UserWithRoleDto;
import com.example.carsharing.supplier.UserSupplier;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerTest {
    protected static MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(
            @Autowired WebApplicationContext applicationContext
    ) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @WithUserDetails(value = "john.doe@example.com",
            userDetailsServiceBeanName = "customUserDetailsService")
    @Test
    @DisplayName("Get user's info test")
    void getMyInfo_GivenUser_Success() throws Exception {
        UserDto expected = UserSupplier.getUserDto();

        MvcResult result = mockMvc.perform(
                        get("/users/me")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        UserDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), UserDto.class);

        assertNotNull(actual);
        EqualsBuilder.reflectionEquals(expected, actual);
    }

    @WithMockUser(username = "admin", authorities = {"MANAGER"})
    @Test
    @DisplayName("Update user's role test")
    @Sql(
            scripts = "classpath:db/user/rollback-user-role.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    void updateUserRole_ValidRoleAndId_Success() throws Exception {
        UserWithRoleDto expected = UserSupplier.getUserWithRoleDtoWithId1();

        MvcResult result = mockMvc.perform(
                        put("/users/1/role?role=MANAGER")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        UserWithRoleDto actual = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(), UserWithRoleDto.class);

        assertNotNull(actual);
        EqualsBuilder.reflectionEquals(expected, actual);
    }

    @WithMockUser(username = "admin", authorities = {"MANAGER"})
    @Test
    @DisplayName("Update user's role with invalid role name test")
    void updateUserRole_InvalidRole_Success() throws Exception {
        mockMvc.perform(
                        put("/users/1/role?role=GOD")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @WithMockUser(username = "admin", authorities = {"MANAGER"})
    @Test
    @DisplayName("Update user's role with invalid id test")
    void updateUserRole_InvalidId_Success() throws Exception {
        mockMvc.perform(
                        put("/users/10/role?role=MANAGER")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();
    }
}
