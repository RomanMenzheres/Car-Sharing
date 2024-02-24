package com.example.carsharing.dto.car;

public record CarWithShortInfoDto(
        Long id,
        String brand,
        String model,
        String type
) {}
