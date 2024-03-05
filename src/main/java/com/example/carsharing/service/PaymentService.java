package com.example.carsharing.service;

import com.example.carsharing.dto.payment.CreatePaymentRequestDto;
import com.example.carsharing.dto.payment.PaymentDto;
import java.util.List;

public interface PaymentService {

    List<PaymentDto> findByUser(Long userId);

    PaymentDto create(CreatePaymentRequestDto requestDto);

    PaymentDto successful(String sessionId);

    PaymentDto canceled(String sessionId);
}
