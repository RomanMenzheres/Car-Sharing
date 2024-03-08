package com.example.carsharing.controller;

import com.example.carsharing.dto.payment.CreatePaymentRequestDto;
import com.example.carsharing.dto.payment.PaymentDto;
import com.example.carsharing.service.PaymentService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/payments")
public class PaymentController {
    private final PaymentService paymentService;

    @GetMapping
    public List<PaymentDto> getByUserId(@RequestParam Long userId) {
        return paymentService.findByUser(userId);
    }

    @PostMapping
    public PaymentDto create(@Valid @RequestBody CreatePaymentRequestDto requestDto) {
        return paymentService.save(requestDto);
    }

    @GetMapping("/success")
    public PaymentDto checkSuccessfulPayments(@RequestParam("session_id") String sessionId) {
        return paymentService.successful(sessionId);
    }

    @GetMapping("/cancel")
    public PaymentDto canceledPayment(@RequestParam("session_id") String sessionId) {
        return paymentService.canceled(sessionId);
    }
}
