package com.example.carsharing.dto.payment;

import java.math.BigDecimal;
import java.net.URL;

public record PaymentDto(
        Long id,
        Long rentalId,
        String status,
        String type,
        URL sessionUrl,
        String sessionId,
        BigDecimal amountToPay
) {}
