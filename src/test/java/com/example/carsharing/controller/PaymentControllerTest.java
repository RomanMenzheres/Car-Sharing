package com.example.carsharing.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.carsharing.dto.payment.CreatePaymentRequestDto;
import com.example.carsharing.dto.payment.PaymentDto;
import com.example.carsharing.supplier.PaymentSupplier;
import com.example.carsharing.util.StripeUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.exception.InvalidRequestException;
import com.stripe.model.checkout.Session;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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

@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PaymentControllerTest {
    protected static MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private StripeUtil stripeUtil;

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
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("db/payment/add-default-payments.sql")
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
                    new ClassPathResource("db/payment/delete-all-payments.sql")
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("db/rental/delete-all-rentals.sql")
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("db/car/delete-all-cars.sql")
            );
        }
    }

    @WithMockUser(username = "admin", authorities = {"MANAGER"})
    @Test
    @DisplayName("Create a new payment with invalid request dto test")
    void createPayment_InvalidRequestDto_BadRequest() throws Exception {
        CreatePaymentRequestDto requestDto = PaymentSupplier.getInvalidCreatePaymentRequestDto();

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(
                        post("/payments")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @WithMockUser(username = "admin", authorities = {"MANAGER"})
    @Test
    @DisplayName("Create a new payment with paid rental test")
    void createPayment_RentalAlreadyPaid_BadRequest() throws Exception {
        CreatePaymentRequestDto requestDto =
                PaymentSupplier.getCreatePaymentRequestDtoWithPaidRental();

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(
                        post("/payments")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @WithMockUser(username = "admin", authorities = {"MANAGER"})
    @Test
    @DisplayName("Get payments by valid user id test")
    void getPaymentsByUserId_ValidId_Success() throws Exception {
        List<PaymentDto> expected = PaymentSupplier.getPaymentsByUserWithId1();

        MvcResult result = mockMvc.perform(
                        get("/payments?user_id=1")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        PaymentDto[] actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), PaymentDto[].class);

        assertEquals(expected.size(), actual.length);
        EqualsBuilder.reflectionEquals(expected, actual);
    }

    @WithMockUser(username = "admin", authorities = {"MANAGER"})
    @Test
    @Sql(
            scripts = "classpath:db/payment/rollback-payment-status.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    @DisplayName("Success payment by valid session id test")
    void successPayment_ValidSessionId_Success() throws Exception {
        String id = "cs_test_a1OOcLaCUOFF9gl6xPr0YEzC5hbRt9hHBZuJc6AnMB2oO6WzEXK9SAHUd7";
        Session session = new Session();
        session.setStatus("complete");
        PaymentDto expected = PaymentSupplier.getSuccessPaymentDto();

        when(stripeUtil.retrieveSession(id)).thenReturn(session);

        MvcResult result = mockMvc.perform(
                        get("/payments/success?session_id=" + id)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        PaymentDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), PaymentDto.class);

        assertNotNull(actual);
        EqualsBuilder.reflectionEquals(expected, actual);
    }

    @WithMockUser(username = "admin", authorities = {"MANAGER"})
    @Test
    @DisplayName("Success payment by invalid session id test")
    void successPayment_InvalidSessionId_NotFound() throws Exception {
        String id = "NotFoundSessionId";

        when(stripeUtil.retrieveSession(id)).thenThrow(InvalidRequestException.class);

        mockMvc.perform(
                        get("/payments/success?session_id=" + id)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @WithMockUser(username = "admin", authorities = {"MANAGER"})
    @Test
    @Sql(
            scripts = "classpath:db/payment/rollback-payment-status.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    @DisplayName("Cancel payment by valid session id test")
    void cancelPayment_ValidSessionId_Success() throws Exception {
        String id = "cs_test_a1OOcLaCUOFF9gl6xPr0YEzC5hbRt9hHBZuJc6AnMB2oO6WzEXK9SAHUd7";
        Session session = new Session();
        session.setStatus("open");
        PaymentDto expected = PaymentSupplier.getCanceledPaymentDto();

        when(stripeUtil.retrieveSession(id)).thenReturn(session);

        MvcResult result = mockMvc.perform(
                        get("/payments/cancel?session_id=" + id)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        PaymentDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), PaymentDto.class);

        assertNotNull(actual);
        EqualsBuilder.reflectionEquals(expected, actual);
    }

    @WithMockUser(username = "admin", authorities = {"MANAGER"})
    @Test
    @DisplayName("Cancel payment by invalid session id test")
    void cancelPayment_InvalidSessionId_NotFound() throws Exception {
        String id = "NotFoundSessionId";

        when(stripeUtil.retrieveSession(id)).thenThrow(InvalidRequestException.class);

        mockMvc.perform(
                        get("/payments/success?session_id=" + id)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();
    }
}
