package com.example.carsharing.service;

import com.example.carsharing.dto.payment.CreatePaymentRequestDto;
import com.example.carsharing.dto.payment.PaymentDto;
import java.util.List;

public interface PaymentService {
    PaymentDto save(CreatePaymentRequestDto requestDto);

    List<PaymentDto> findByUser(Long userId);

    PaymentDto succeed(String sessionId);

    PaymentDto cancel(String sessionId);
}
