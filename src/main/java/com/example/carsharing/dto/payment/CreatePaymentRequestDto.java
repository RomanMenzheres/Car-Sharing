package com.example.carsharing.dto.payment;

import com.example.carsharing.model.Payment;
import com.example.carsharing.validation.ValueOfEnum;
import jakarta.validation.constraints.Positive;

public record CreatePaymentRequestDto(
        @Positive
        Long rentalId,
        @ValueOfEnum(enumClass = Payment.PaymentType.class)
        String type
) {}
