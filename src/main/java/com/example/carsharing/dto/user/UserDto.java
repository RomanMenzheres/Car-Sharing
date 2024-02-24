package com.example.carsharing.dto.user;

public record UserDto(
        Long id,
        String email,
        String firstName,
        String lastName
) {}
