package com.example.carsharing.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;

public record UserLoginRequestDto(
        @Email
        String email,
        @Min(8)
        String password
) {}
