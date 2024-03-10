package com.example.carsharing.controller;

import com.example.carsharing.dto.payment.CreatePaymentRequestDto;
import com.example.carsharing.dto.payment.PaymentDto;
import com.example.carsharing.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/payments")
@Tag(name = "Payment Management", description = "Endpoint for payments")
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentDto create(@Valid @RequestBody CreatePaymentRequestDto requestDto) {
        return paymentService.save(requestDto);
    }

    @Operation(summary = "Get User's Payments", description = "Get all payments by user id")
    @GetMapping
    public List<PaymentDto> getByUserId(@RequestParam Long userId) {
        return paymentService.findByUser(userId);
    }

    @Operation(summary = "Payment Successful", description = "Success endpoint for payment")
    @GetMapping("/success")
    public PaymentDto checkSuccessfulPayments(@RequestParam("session_id") String sessionId) {
        return paymentService.successful(sessionId);
    }

    @Operation(summary = "Payment Canceled", description = "Cancel endpoint for payment")
    @GetMapping("/cancel")
    public PaymentDto canceledPayment(@RequestParam("session_id") String sessionId) {
        return paymentService.canceled(sessionId);
    }
}
