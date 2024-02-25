package com.example.carsharing.dto;

public record CarWithShortInfoDto(
        Long id,
        String brand,
        String model,
        String type
) {}
