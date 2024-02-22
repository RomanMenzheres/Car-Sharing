package com.example.carsharing.dto;

public record CarDtoWithShortInfo(
        Long id,
        String brand,
        String model,
        String type
) {}
