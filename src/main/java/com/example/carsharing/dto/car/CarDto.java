package com.example.carsharing.dto.car;

import java.math.BigDecimal;

public record CarDto(
        Long id,
        String brand,
        String model,
        String type,
        int inventory,
        BigDecimal dailyFee
) {}
